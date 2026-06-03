package plain.bookmaru.domain.notification.vo

data class NotificationInfo(
    val name: String,
    val payload: NotificationPayload,
    val type: NotificationType,
    val url: String
)
