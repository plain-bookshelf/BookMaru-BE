package plain.bookmaru.global.sse

import java.time.LocalDateTime

data class SseConnectResponse(
    val status: String = "connected",
    val connectedAt: LocalDateTime = LocalDateTime.now()
)
