package plain.bookmaru.domain.display.persistent.wrapper

import kotlinx.serialization.Serializable
import plain.bookmaru.domain.display.port.out.result.EventInfoResult

@Serializable
data class EventListWrapper(val events: List<EventInfoResult>)