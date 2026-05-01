package plain.bookmaru.domain.notification.presentation.dto.response

import plain.bookmaru.domain.notification.model.Notification

data class NotificationSnapshotResponse(
    val notifications: List<Notification>
)