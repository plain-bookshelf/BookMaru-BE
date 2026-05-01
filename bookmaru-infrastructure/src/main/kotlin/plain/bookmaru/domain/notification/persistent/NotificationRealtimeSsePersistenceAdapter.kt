package plain.bookmaru.domain.notification.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.port.out.NotificationRealtimePort
import plain.bookmaru.domain.notification.presentation.dto.response.NotificationSseResponse
import plain.bookmaru.global.sse.NotificationSseEmitterManager

@Component
class NotificationRealtimeSsePersistenceAdapter(
    private val notificationSseEmitterManager: NotificationSseEmitterManager
) : NotificationRealtimePort {

    override fun send(notification: Notification) {
        val memberId = notification.memberId

        notificationSseEmitterManager.sendToMember(
            memberId = memberId,
            eventName = "notification",
            data = NotificationSseResponse(notification)
        )
    }
}