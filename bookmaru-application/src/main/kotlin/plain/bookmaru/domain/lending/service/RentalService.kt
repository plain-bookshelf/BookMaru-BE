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
    private val concurrencyPort: ConcurrencyPort
) : RentalUseCase {
    override suspend fun execute(command: LendingCommand) {
        concurrencyPort.executeWithRetry("책 대여 서비스") {
            val bookAffiliationId = command.bookAffiliationId
            val username = command.username

            val member = memberPort.findByUsername(username)
                ?: throw NotFoundMemberException("$username 아이디를 사용하는 유저를 찾지 못 했습니다.")

            if (member.authority == Authority.ROLE_OVERDUE)
                throw OverdueException("연체 상태이기에 대여할 수 없습니다.")

            val count = member.lendingBook.rentalCount
            val availReservationBook = when (member.authority) {
                Authority.ROLE_USER -> 3
                Authority.ROLE_MANAGER -> 10
                else -> 1000
            }

            if (count >= availReservationBook)
                throw NoMoreRentalException("$username 아이디의 유저는 더 이상 책을 대여할 수 없습니다.")

            val bookDetail = bookDetailPort.findRentalBookDetailByBookAffiliationId(bookAffiliationId)
            log.info { "책 정보를 찾아오는데 성공했습니다." }

            if (bookDetail == null)
                throw NotExistBookDetailException("bookAffiliationId: $bookAffiliationId 에서 대여할 수 있는 책 정보가 없습니다.")

            val rental = Rental(
                memberId = member.id!!,
                bookDetailId = bookDetail.id!!,
                bookRecord = BookRecord(
                    rentalDate = LocalDateTime.now(),
                )
            )

            member.incrementRentalCount()
            val returnDate = if (member.authority == Authority.ROLE_TEACHER || member.authority == Authority.ROLE_LIBRARIAN)
                LocalDate.now().plusDays(365)
            else
                LocalDate.now().plusDays(14)

            transactionPort.withTransaction {
                bookDetailPort.updateRental(rental, returnDate)
                log.info { "책 대여자 정보와 책 상태 변경에 성공했습니다." }
                memberPort.save(member)
                log.info { "유저 대여한 책 권 수 증가 완료" }
                bookRentalRecordPort.save(rental)
                log.info { "대여 기록을 남기는데 성공했습니다." }
            }
        }
    }
}