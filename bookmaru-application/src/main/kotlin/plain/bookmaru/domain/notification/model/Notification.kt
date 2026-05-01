package plain.bookmaru.domain.notification.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.notification.vo.NotificationInfo
import plain.bookmaru.domain.notification.vo.TargetInfo

@Aggregate
class Notification(
    val id: Long? = null,
    val memberId: Long,
    val targetInfo: TargetInfo,
    val notificationInfo: NotificationInfo,
    val isRead: Boolean
)