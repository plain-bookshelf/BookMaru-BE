package plain.bookmaru.domain.event.port.`in`.command

data class EventDeleteCommand(
    val memberId: Long,
    val eventId: Long
)
