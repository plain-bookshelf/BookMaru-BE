package plain.bookmaru.domain.manager.scheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.port.out.result.OverdueNotificationTarget
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.port.out.NotificationPort
import plain.bookmaru.domain.notification.port.out.NotificationPushPort
import plain.bookmaru.domain.notification.port.out.NotificationRealtimePort
import plain.bookmaru.domain.notification.vo.NotificationInfo
import plain.bookmaru.domain.notification.vo.NotificationPayload
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.domain.notification.vo.TargetInfo
import plain.bookmaru.domain.notification.vo.TargetType
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class OverdueStatusScheduler(
    private val bookRentalRecordPort: BookRentalRecordPort,
    private val notificationPort: NotificationPort,
    private val memberPort: MemberPort,
    private val notificationRealtimePort: NotificationRealtimePort,
    private val notificationPushPort: NotificationPushPort,
    private val transactionPort: TransactionPort
) {

    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    suspend fun checkOverdueStatus() {
        val today = LocalDate.now()
        val now = LocalDateTime.now()
        val overdueTargets = bookRentalRecordPort.findOverdueNotificationTargets(today)
        val activeOverdueMemberIds = overdueTargets.map { it.memberId }.distinct()

        val notificationTargets = overdueTargets.filterNot { target ->
            notificationPort.existsByMemberAndTargetAndType(
                memberId = target.memberId,
                targetId = target.bookDetailId,
                targetType = TargetType.BOOK,
                type = NotificationType.OVERDUE
            )
        }

        val releasedCount = memberPort.releaseExpiredOverduePenalties(now, activeOverdueMemberIds)
        val markedCount = memberPort.markOverdueMembers(activeOverdueMemberIds)
        val savedNotifications = transactionPort.withTransaction {
            notificationTargets
                .map(::createOverdueNotification)
                .map(notificationPort::save)
        }

        log.info {
            "연체 상태 점검을 완료했습니다. active=${activeOverdueMemberIds.size}, marked=$markedCount, released=$releasedCount, notification=${savedNotifications.size}"
        }

        savedNotifications.forEach { notification ->
            runCatching {
                notificationRealtimePort.send(notification)
                notificationPushPort.send(notification)
            }.onFailure {
                log.warn(it) {
                    "연체 알림 전송에 실패했습니다. notificationId=${notification.id}, memberId=${notification.memberId}"
                }
            }
        }
    }

    private fun createOverdueNotification(target: OverdueNotificationTarget): Notification {
        return Notification(
            memberId = target.memberId,
            targetInfo = TargetInfo(
                targetId = target.bookDetailId,
                targetType = TargetType.BOOK
            ),
            notificationInfo = NotificationInfo(
                name = "도서가 연체되었습니다.",
                payload = NotificationPayload.OverduePayload(
                    bookId = target.bookAffiliationId,
                    title = target.title,
                    returnDate = target.returnDate.toString()
                ),
                type = NotificationType.OVERDUE,
                url = "/book/${target.bookAffiliationId}"
            ),
            isRead = false
        )
    }
}
