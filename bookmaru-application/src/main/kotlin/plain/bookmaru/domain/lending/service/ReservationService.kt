package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.lending.exception.CannotReserveAvailableBookException
import plain.bookmaru.domain.lending.exception.NoMoreReservationException
import plain.bookmaru.domain.lending.exception.OverdueException
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.port.`in`.ReservationUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class ReservationService(
    private val membersPort: MemberPort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val bookDetailPort: BookDetailPort,
    private val bookReservationPort: BookReservationPort,
    private val concurrencyPort: ConcurrencyPort,
    private val transactionPort: TransactionPort
) : ReservationUseCase {
    override suspend fun execute(command: LendingCommand) {
        concurrencyPort.executeWithRetry("reservation-service") {
            val username = command.username
            val bookAffiliationId = command.bookAffiliationId

            val member = membersPort.findByUsername(username)
                ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")

            if (member.overdueStatus) {
                throw OverdueException("연체 상태에서는 예약할 수 없습니다.")
            }

            val count = member.lendingBook.reservationCount + member.lendingBook.rentalCount
            val availableReservationCount = when (member.authority) {
                Authority.ROLE_USER -> 3
                Authority.ROLE_MANAGER -> 10
                else -> 1000
            }

            if (count >= availableReservationCount) {
                throw NoMoreReservationException("더 이상 예약할 수 없습니다.")
            }

            val availableBook = bookDetailPort.findRentalBookDetailByBookAffiliationId(bookAffiliationId)
            if (availableBook != null) {
                throw CannotReserveAvailableBookException("대여 가능한 책이 있어 예약할 수 없습니다.")
            }

            val waitingRank = bookReservationPort.waiting(bookAffiliationId)

            val reservation = Reservation(
                waitingRank = waitingRank,
                bookAffiliationId = bookAffiliationId,
                member = member
            )

            member.incrementReservationCount()

            transactionPort.withTransaction {
                bookReservationPort.save(reservation)
                log.info { "책 예약에 성공했습니다." }
                membersPort.save(member)
                log.info { "회원 예약 카운트 증가에 성공했습니다." }
                bookAffiliationPort.incrementReservationCount(bookAffiliationId)
                log.info { "책 소속 예약 카운트 증가에 성공했습니다." }
            }
        }
    }
}
