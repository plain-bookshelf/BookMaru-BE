package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.auth.vo.Authority
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
    private val bookReservationPort: BookReservationPort,
    private val concurrencyPort: ConcurrencyPort,
    private val transactionPort: TransactionPort
): ReservationUseCase {
    override suspend fun execute(command: LendingCommand) {
        concurrencyPort.executeWithRetry("책 예약 서비스") {
            val username = command.username
            val bookAffiliationId = command.bookAffiliationId

            val member = membersPort.findByUsername(username)
                ?: throw NotFoundMemberException("$username 을 사용하는 유저를 찾지 못 했습니다.")

            if (member.authority == Authority.ROLE_OVERDUE)
                throw OverdueException("연체 상태이기에 대여할 수 없습니다.")

            val count = member.lendingBook.reservationCount + member.lendingBook.rentalCount
            val availReservationBook = when (member.authority) {
                Authority.ROLE_USER -> 3
                Authority.ROLE_MANAGER -> 10
                else -> 1000
            }

            if (count >= availReservationBook)
                throw NoMoreReservationException("bookAffiliation: $bookAffiliationId 아이디의 책을 $username 아이디의 유저가 대여 횟수 및 예약 횟수가 많아 실패했습니다.")

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
                log.info { "유저 대여한 책 권 수 증가 완료" }
            }
        }
    }
}