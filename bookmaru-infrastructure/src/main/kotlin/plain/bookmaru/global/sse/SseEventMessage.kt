package plain.bookmaru.global.sse

import java.time.LocalDateTime

data class SseEventMessage(
    val id: Long,
    val eventName: String,
    val data: Any,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
