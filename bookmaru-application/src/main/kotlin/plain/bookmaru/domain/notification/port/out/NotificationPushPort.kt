package plain.bookmaru.domain.notification.port.out

import plain.bookmaru.domain.notification.model.Notification

interface NotificationPushPort {
    fun send(notification: Notification)
}
