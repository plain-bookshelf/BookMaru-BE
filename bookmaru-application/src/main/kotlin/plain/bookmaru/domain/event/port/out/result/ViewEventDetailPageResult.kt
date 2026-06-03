package plain.bookmaru.domain.event.port.out.result

import plain.bookmaru.domain.event.vo.EventType
import java.time.LocalDateTime

data class ViewEventDetailPageResult(
    val title: String,
    val status: EventType,
    val imageUrl: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val content: String
)
