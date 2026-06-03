package plain.bookmaru.domain.event.port.`in`.command

import java.time.LocalDateTime

data class EventChangeCommand(
    val memberId: Long,
    val eventId: Long,
    val title: String,
    val content: String,
    val imageUrl: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)
