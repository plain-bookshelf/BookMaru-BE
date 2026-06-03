package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.lending.exception.NotFoundReservationException
import plain.bookmaru.domain.lending.port.`in`.CancelReservationUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class CancelReservationService(
    private val memberPort: MemberPort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val bookReservationPort: BookReservationPort,
    private val transactionPort: TransactionPort,
    private val concurrencyPort: ConcurrencyPort
) : CancelReservationUseCase {
    override suspend fun execute(command: LendingCommand) {
        concurrencyPort.executeWithRetry("cancel-reservation-service") {
            val username = command.username
            val bookAffiliationId = command.bookAffiliationId

            val member = memberPort.findByUsername(username)
                ?: throw NotFoundMemberException("$username 사용자를 찾을 수 없습니다.")

            val reservation = bookReservationPort.findReservation(bookAffiliationId, member.id!!)
                ?: throw NotFoundReservationException("bookAffiliationId: $bookAffiliationId 에 대한 예약 정보를 찾을 수 없습니다.")

            member.decrementReservationCount()

            transactionPort.withTransaction {
                bookReservationPort.deleteReservation(member.id, reservation.bookAffiliationId)
                bookAffiliationPort.decrementReservationCount(reservation.bookAffiliationId)
                memberPort.save(member)
                log.info { "예약을 취소했습니다. memberId=${member.id}, bookAffiliationId=${reservation.bookAffiliationId}" }
            }
        }
    }
}
