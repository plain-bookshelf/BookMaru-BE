package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
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
    private val bookReservationPort: BookReservationPort,
    private val concurrencyPort: ConcurrencyPort,
    private val transactionPort: TransactionPort
) : ReservationUseCase {
    override suspend fun execute(command: LendingCommand) {
        concurrencyPort.executeWithRetry("reservation-service") {
            val username = command.username
            val bookAffiliationId = command.bookAffiliationId

            val member = membersPort.findByUsername(username)
                ?: throw NotFoundMemberException("$username 사용자를 찾을 수 없습니다.")

            if (member.authority == Authority.ROLE_OVERDUE) {
                throw OverdueException("연체 상태에서는 예약할 수 없습니다.")
            }

            val count = member.lendingBook.reservationCount + member.lendingBook.rentalCount
            val availableReservationCount = when (member.authority) {
                Authority.ROLE_USER -> 3
                Authority.ROLE_MANAGER -> 10
                else -> 1000
            }

            if (count >= availableReservationCount) {
                throw NoMoreReservationException(
                    "bookAffiliationId: $bookAffiliationId 에 대해 $username 사용자는 더 이상 예약할 수 없습니다."
                )
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
                log.info { "책 예약에 성공했습니다.." }
                bookAffiliationPort.incrementReservationCount(bookAffiliationId)
                log.info { "책 소속 예약자 카운트 증가를 성공했습니다." }
            }
        }
    }
}
