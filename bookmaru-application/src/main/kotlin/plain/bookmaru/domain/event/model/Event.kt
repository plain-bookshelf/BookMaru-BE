package plain.bookmaru.domain.event.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.event.vo.EventContent
import plain.bookmaru.domain.event.vo.EventInfo

@Aggregate
class Event(
    val id: Long? = null,
    val memberId: Long,
    eventInfo: EventInfo,
    eventContent: EventContent
) {
    var eventInfo = eventInfo
        private set

    var eventContent = eventContent
        private set

    fun updateEventInfo(eventInfo: EventInfo, content: EventContent) {
        this.eventInfo = eventInfo
        this.eventContent = content
    }
}