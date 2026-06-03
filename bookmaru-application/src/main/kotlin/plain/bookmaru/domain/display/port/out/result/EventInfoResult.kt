package plain.bookmaru.domain.display.port.out.result

import kotlinx.serialization.Serializable

@Serializable
data class EventInfoResult(
    val imageUrl: String,
    val id: Long
)