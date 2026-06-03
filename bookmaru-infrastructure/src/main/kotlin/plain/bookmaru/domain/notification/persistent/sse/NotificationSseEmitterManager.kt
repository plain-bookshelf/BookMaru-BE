package plain.bookmaru.domain.notification.persistent.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import plain.bookmaru.global.sse.BaseSseEmitterManager

@Component
class NotificationSseEmitterManager : BaseSseEmitterManager(
    channelName = "SSE",
    targetLabel = "memberId"
) {

    fun subscribe(memberId: Long, lastEventId: String?): SseEmitter {
        return subscribeInternal(memberId, lastEventId)
    }

    fun sendToMember(memberId: Long, eventName: String, data: Any) {
        sendToTargetInternal(memberId, eventName, data)
    }
}