package plain.bookmaru.domain.event.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.event.vo.EventContent
import plain.bookmaru.domain.event.vo.EventInfo

@Aggregate
class Event(
    val id: Long? = null,
    val memberId: Long,
    val eventInfo: EventInfo,
    val eventContent: EventContent
) {
}