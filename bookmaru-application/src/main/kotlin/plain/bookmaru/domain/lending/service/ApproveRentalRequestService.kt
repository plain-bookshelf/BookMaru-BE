package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.inventory.exception.NotFoundBookDetailException
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.lending.exception.NotFoundRentalRecordException
import plain.bookmaru.domain.lending.port.`in`.ApproveRentalRequestUseCase
import plain.bookmaru.domain.lending.port.`in`.command.ApproveRentalRequestCommand
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.port.out.result.RentalRequestApprovalInfo
import plain.bookmaru.domain.manager.port.out.RentalRequestRealtimePort
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.port.`in`.PublishNotificationUseCase
import plain.bookmaru.domain.notification.vo.NotificationInfo
import plain.bookmaru.domain.notification.vo.NotificationPayload
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.domain.notification.vo.TargetInfo
import plain.bookmaru.domain.notification.vo.TargetType

private val log = KotlinLogging.logger {}

@Service
class ApproveRentalRequestService(
    private val bookRentalRecordPort: BookRentalRecordPort,
    private val bookDetailPort: BookDetailPort,
    private val publishNotificationUseCase: PublishNotificationUseCase,
    private val rentalRequestRealtimePort: RentalRequestRealtimePort,
    private val transactionPort: TransactionPort,
    private val concurrencyPort: ConcurrencyPort
) : ApproveRentalRequestUseCase {

    override suspend fun execute(command: ApproveRentalRequestCommand) {
        val requestInfo = concurrencyPort.executeWithRetry("approve-rental-request-service") {
            val requestInfo = bookRentalRecordPort.findRentalRequestApprovalInfo(
                bookDetailId = command.bookDetailId,
                affiliationId = command.affiliationId
            ) ?: throw NotFoundRentalRecordException(
                "bookDetailId: ${command.bookDetailId} 대여 요청 정보를 찾을 수 없습니다."
            )

            val bookDetail = bookDetailPort.findRentalRequestBookDetailById(
                bookDetailId = command.bookDetailId,
                affiliationId = command.affiliationId
            ) ?: throw NotFoundBookDetailException("bookDetailId: ${command.bookDetailId} 승인 가능한 대여 요청을 찾을 수 없습니다.")

            val approvedBookDetail = bookDetail.approveRental()

            val updatedCount = transactionPort.withTransaction {
                bookDetailPort.approveRentalRequest(approvedBookDetail)
            }

            if (updatedCount == 0L) {
                throw NotFoundBookDetailException("bookDetailId: ${command.bookDetailId} 승인 가능한 대여 요청을 찾을 수 없습니다.")
            }

            requestInfo
        }

        sendRentalApprovedNotification(requestInfo)
        sendRentalRequestRealtimeUpdate(command.affiliationId)

        log.info { "대여 요청을 승인했습니다. bookDetailId=${command.bookDetailId}, memberId=${requestInfo.memberId}" }
    }

    private suspend fun sendRentalApprovedNotification(requestInfo: RentalRequestApprovalInfo) {
        runCatching {
            publishNotificationUseCase.execute(
                Notification(
                    memberId = requestInfo.memberId,
                    targetInfo = TargetInfo(
                        targetId = requestInfo.bookDetailId,
                        targetType = TargetType.BOOK
                    ),
                    notificationInfo = NotificationInfo(
                        name = "대여 요청이 승인되었습니다.",
                        payload = NotificationPayload.RentalPayload(
                            bookId = requestInfo.bookAffiliationId,
                            title = requestInfo.title,
                            returnDate = requestInfo.returnDate.toString()
                        ),
                        type = NotificationType.RENTAL,
                        url = "/book/${requestInfo.bookAffiliationId}"
                    ),
                    isRead = false
                )
            )
        }.onFailure {
            log.warn(it) { "대여 승인 알림 발행에 실패했습니다. bookDetailId=${requestInfo.bookDetailId}, memberId=${requestInfo.memberId}" }
        }
    }

    private suspend fun sendRentalRequestRealtimeUpdate(affiliationId: Long) {
        runCatching {
            val updatedRequests = bookRentalRecordPort.findRentalRequestBookByAffiliationId(affiliationId).orEmpty()
            rentalRequestRealtimePort.send(affiliationId, updatedRequests)
        }.onFailure {
            log.warn(it) { "관리자 대여 요청 SSE 전송에 실패했습니다. affiliationId=$affiliationId" }
        }
    }
}
