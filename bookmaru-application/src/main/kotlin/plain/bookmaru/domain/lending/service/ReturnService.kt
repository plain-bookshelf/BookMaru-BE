package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.inventory.exception.NotFoundBookDetailException
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.port.`in`.ReturnUseCase
import plain.bookmaru.domain.lending.port.`in`.command.ReturnCommand
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.lending.vo.BookRecord
import plain.bookmaru.domain.member.port.out.MemberPort
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class ReturnService(
    private val bookDetailPort: BookDetailPort,
    private val bookRentalRecordPort: BookRentalRecordPort,
    private val bookReservationPort: BookReservationPort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val memberPort: MemberPort,
    private val transactionPort: TransactionPort,
    private val concurrencyPort: ConcurrencyPort
) : ReturnUseCase {
    override suspend fun execute(command: ReturnCommand) {
        concurrencyPort.executeWithRetry("return-service") {
            log.info { "반납 처리를 시작합니다." }

            val bookDetail = bookDetailPort.findRentalBookByBookDetailId(command.bookDetailId)
                ?: throw NotFoundBookDetailException("책 상세 정보를 찾을 수 없습니다.")

            val reservation = bookReservationPort.findFirstReservationByBookAffiliationId(bookDetail.bookAffiliationId)

            transactionPort.withTransaction {
                bookRentalRecordPort.completeReturn(command.bookDetailId)
                log.info { "반납 기록을 완료하고 책 상태를 초기화했습니다." }

                if (reservation != null) {
                    assignReturnedBookToFirstReservation(command.bookDetailId, reservation)
                }
            }
        }
    }

    private fun assignReturnedBookToFirstReservation(
        bookDetailId: Long,
        reservation: Reservation
    ) {
        val reservationMember = reservation.member
        val returnDate = calculateReturnDate(reservationMember.authority)

        reservationMember.decrementReservationCount()
        reservationMember.incrementRentalCount()

        val autoRental = Rental(
            memberId = reservationMember.id!!,
            bookDetailId = bookDetailId,
            bookRecord = BookRecord(
                rentalDate = LocalDateTime.now()
            )
        )

        bookReservationPort.deleteReservation(reservationMember.id, reservation.bookAffiliationId)
        bookAffiliationPort.decrementReservationCount(reservation.bookAffiliationId)
        memberPort.save(reservationMember)
        bookDetailPort.assignReturnedRental(bookDetailId, reservationMember.id, returnDate)
        bookRentalRecordPort.save(autoRental)

        log.info {
            "반납된 책을 첫 예약자에게 자동 대여했습니다. bookDetailId=$bookDetailId, memberId=${reservationMember.id}"
        }
    }

    private fun calculateReturnDate(authority: Authority): LocalDate {
        return if (authority == Authority.ROLE_TEACHER || authority == Authority.ROLE_LIBRARIAN) {
            LocalDate.now().plusDays(365)
        } else {
            LocalDate.now().plusDays(14)
        }
    }
}
