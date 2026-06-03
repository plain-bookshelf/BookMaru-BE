package plain.bookmaru.domain.notification.presentation.dto.response

import plain.bookmaru.domain.notification.model.Notification

data class
NotificationSseResponse(
    val notification: Notification
)