package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.inventory.exception.NotFoundBookDetailException
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.lending.port.`in`.RentalUseCase
import plain.bookmaru.domain.lending.port.`in`.ReturnUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.domain.lending.port.`in`.command.ReturnCommand
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.port.out.BookReservationPort

private val log = KotlinLogging.logger {}

@Service
class ReturnService(
    private val bookDetailPort: BookDetailPort,
    private val bookRentalRecordPort: BookRentalRecordPort,
    private val bookReservationPort: BookReservationPort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val transactionPort: TransactionPort,
    private val rentalUseCase: RentalUseCase
): ReturnUseCase {
    override suspend fun execute(command: ReturnCommand) {
        log.info { "책 반납 로직 시작" }
        val affiliationId = command.affiliationId

        val bookDetail = bookDetailPort.findRentalBookByBookDetailId(command.bookDetailId)
            ?: throw NotFoundBookDetailException("책 상세 정보를 찾지 못 했습니다.")

        bookRentalRecordPort.update(bookDetail)

        val reservation = bookReservationPort.findFirstReservationByAffiliationId(affiliationId)

        if (reservation == null) return

        val memberId = reservation.member.id
        val bookAffiliationId = reservation.bookAffiliationId
        val lendingCommand = LendingCommand(
            username = reservation.member.accountInfo?.username!!,
            bookAffiliationId = bookAffiliationId
        )

        rentalUseCase.execute(lendingCommand)
        transactionPort.withTransaction {
            bookReservationPort.deleteReservation(memberId!!, bookAffiliationId)
            bookAffiliationPort.decrementReservationCount(bookAffiliationId)
        }
    }
}