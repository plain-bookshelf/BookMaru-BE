package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.lending.exception.NoMoreRentalException
import plain.bookmaru.domain.lending.exception.NotExistBookDetailException
import plain.bookmaru.domain.lending.exception.OverdueException
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.port.`in`.RentalUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.vo.BookRecord
import plain.bookmaru.domain.manager.port.out.RentalRequestRealtimePort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class RentalService(
    private val bookDetailPort: BookDetailPort,
    private val memberPort: MemberPort,
    private val bookRentalRecordPort: BookRentalRecordPort,
    private val transactionPort: TransactionPort,
    private val concurrencyPort: ConcurrencyPort,
    private val rentalRequestRealtimePort: RentalRequestRealtimePort
) : RentalUseCase {
    override suspend fun execute(command: LendingCommand) {
        val affiliationId = concurrencyPort.executeWithRetry("rental-service") {
            val bookAffiliationId = command.bookAffiliationId
            val username = command.username

            val member = memberPort.findByUsername(username)
                ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
            val memberId = requireNotNull(member.id) { "회원 식별자 정보가 없습니다." }

            if (member.overdueStatus) {
                throw OverdueException("연체 상태에서는 대여할 수 없습니다.")
            }

            val availableRentalCount = when (member.authority) {
                Authority.ROLE_USER -> 3
                Authority.ROLE_MANAGER -> 10
                else -> 1000
            }

            if (member.lendingBook.rentalCount >= availableRentalCount) {
                throw NoMoreRentalException("더 이상 대여할 수 없습니다.")
            }

            val bookDetail = bookDetailPort.findRentalBookDetailByBookAffiliationId(bookAffiliationId)
                ?: throw NotExistBookDetailException("대여 가능한 책이 없습니다.")

            val rental = Rental(
                memberId = memberId,
                bookDetailId = bookDetail.id!!,
                bookRecord = BookRecord(
                    rentalDate = LocalDateTime.now()
                )
            )

            val returnDate = if (member.authority == Authority.ROLE_TEACHER || member.authority == Authority.ROLE_LIBRARIAN) {
                LocalDate.now().plusDays(365)
            } else {
                LocalDate.now().plusDays(14)
            }

            val requestedBookDetail = bookDetail.requestRental(memberId, returnDate)

            transactionPort.withTransaction {
                val updatedCount = bookDetailPort.updateRental(requestedBookDetail)
                if (updatedCount == 0L) {
                    throw NotExistBookDetailException("대여 가능한 책이 없습니다.")
                }

                memberPort.save(member)
                bookRentalRecordPort.save(rental)
            }

            member.affiliationId
        }

        if (affiliationId != null) {
            runCatching {
                val updatedRequests = bookRentalRecordPort.findRentalRequestBookByAffiliationId(affiliationId).orEmpty()
                rentalRequestRealtimePort.send(affiliationId, updatedRequests)
            }.onFailure {
                log.warn(it) { "관리자 대여 요청 SSE 전송에 실패했습니다. affiliationId=$affiliationId" }
            }
        }
    }
}
