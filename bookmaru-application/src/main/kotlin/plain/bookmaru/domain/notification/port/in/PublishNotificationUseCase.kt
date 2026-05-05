package plain.bookmaru.domain.notification.port.`in`

import plain.bookmaru.domain.notification.model.Notification

interface PublishNotificationUseCase {
    suspend fun execute(notification: Notification): Notification
    suspend fun executeAll(notifications: List<Notification>): List<Notification>
}
