package plain.bookmaru.domain.manager.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import plain.bookmaru.global.sse.BaseSseEmitterManager

@Component
class ManagerRentalRequestEmitterManager : BaseSseEmitterManager(
    channelName = "관리자 대여 요청 SSE",
    targetLabel = "affiliationId"
) {

    fun subscribe(affiliationId: Long, lastEventId: String?): SseEmitter {
        return subscribeInternal(affiliationId, lastEventId)
    }

    fun sendToAffiliation(affiliationId: Long, eventName: String, data: Any) {
        sendToTargetInternal(affiliationId, eventName, data)
    }
}
