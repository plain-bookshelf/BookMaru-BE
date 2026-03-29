package plain.bookmaru.domain.lending.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.lending.exception.NoMoreReservationException
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.port.`in`.ReservationUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort

@Service
class ReservationService(
    private val membersPort: MemberPort,
    private val bookReservationPort: BookReservationPort,
    private val concurrencyPort: ConcurrencyPort
): ReservationUseCase {
    override suspend fun execute(command: LendingCommand) {
        concurrencyPort.executeWithRetry("책 예약 서비스") {
            val username = command.username
            val bookAffiliationId = command.bookAffiliationId

            val member = membersPort.findByUsername(username)
                ?: throw NotFoundMemberException("$username 을 사용하는 유저를 찾지 못 했습니다.")

            val count = member.lendingBook.reservationCount + member.lendingBook.rentalCount
            val availReservationBook = if (member.authority == Authority.ROLE_USER) 3 else if (member.authority == Authority.ROLE_MANAGER) 10 else 1000
            if (count > availReservationBook)
                throw NoMoreReservationException("bookAffiliation: $bookAffiliationId 아이디의 책을 $username 아이디의 유저가 대여 횟수 및 예약 횟수가 많아 실패했습니다.")

            val waitingRank = bookReservationPort.waiting(bookAffiliationId)

            val reservation = Reservation(
                waitingRank = waitingRank,
                bookAffiliationId = bookAffiliationId,
                memberId = member.id!!
            )

            bookReservationPort.save(reservation)
        }
    }
}