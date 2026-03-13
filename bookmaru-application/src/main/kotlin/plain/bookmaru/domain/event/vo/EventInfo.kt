package plain.bookmaru.domain.event.vo

import java.time.LocalDateTime

data class EventInfo(
    val title: String,
    val status: EventType,
    val imageUrl: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)
