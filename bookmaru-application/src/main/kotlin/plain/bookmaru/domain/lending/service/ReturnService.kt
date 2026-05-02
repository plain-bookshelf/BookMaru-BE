package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.inventory.exception.NotFoundBookDetailException
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.inventory.port.out.result.BookNotificationInfo
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.port.`in`.ReturnUseCase
import plain.bookmaru.domain.lending.port.`in`.command.ReturnCommand
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.lending.vo.BookRecord
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.port.`in`.PublishNotificationUseCase
import plain.bookmaru.domain.notification.vo.NotificationInfo
import plain.bookmaru.domain.notification.vo.NotificationPayload
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.domain.notification.vo.TargetInfo
import plain.bookmaru.domain.notification.vo.TargetType
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
    private val publishNotificationUseCase: PublishNotificationUseCase,
    private val transactionPort: TransactionPort,
    private val concurrencyPort: ConcurrencyPort
) : ReturnUseCase {
    override suspend fun execute(command: ReturnCommand) {
        val reservationNotification = concurrencyPort.executeWithRetry("return-service") {
            log.info { "반납 처리를 시작합니다." }

            val bookDetail = bookDetailPort.findRentalBookByBookDetailId(command.bookDetailId)
                ?: throw NotFoundBookDetailException("책 상세 정보를 찾을 수 없습니다.")
            val bookInfo = bookDetailPort.findBookNotificationInfoByBookDetailId(command.bookDetailId)
            var notification: Notification? = null

            transactionPort.withTransaction {
                bookRentalRecordPort.completeReturn(command.bookDetailId)
                log.info { "반납 기록을 완료하고 책 상태를 초기화했습니다." }

                val reservation = bookReservationPort
                    .findFirstReservationByBookAffiliationIdForUpdate(bookDetail.bookAffiliationId)

                if (reservation != null) {
                    notification = assignReturnedBookToFirstReservation(
                        bookDetailId = command.bookDetailId,
                        reservation = reservation,
                        bookInfo = bookInfo
                    )
                }
            }

            notification
        }

        reservationNotification?.let { notification ->
            runCatching {
                publishNotificationUseCase.execute(notification)
            }.onFailure {
                log.warn(it) { "예약 자동 대여 알림 발행에 실패했습니다. memberId=${notification.memberId}, targetId=${notification.targetInfo.targetId}" }
            }
        }
    }

    private fun assignReturnedBookToFirstReservation(
        bookDetailId: Long,
        reservation: Reservation,
        bookInfo: BookNotificationInfo?
    ): Notification? {
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

        if (bookInfo == null) return null

        return Notification(
            memberId = reservationMember.id,
            targetInfo = TargetInfo(
                targetId = bookDetailId,
                targetType = TargetType.BOOK
            ),
            notificationInfo = NotificationInfo(
                name = "예약한 책의 대여가 완료되었습니다.",
                payload = NotificationPayload.ReservationPayload(
                    bookId = bookInfo.bookAffiliationId,
                    title = bookInfo.title,
                    returnDate = returnDate.toString()
                ),
                type = NotificationType.RESERVATION,
                url = "/book/${bookInfo.bookAffiliationId}"
            ),
            isRead = false
        )
    }

    private fun calculateReturnDate(authority: Authority): LocalDate {
        return if (authority == Authority.ROLE_TEACHER || authority == Authority.ROLE_LIBRARIAN) {
            LocalDate.now().plusDays(365)
        } else {
            LocalDate.now().plusDays(14)
        }
    }
}
