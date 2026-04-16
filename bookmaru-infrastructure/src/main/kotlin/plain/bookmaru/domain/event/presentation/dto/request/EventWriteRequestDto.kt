package plain.bookmaru.domain.event.presentation.dto.request

import plain.bookmaru.domain.event.port.`in`.command.EventChangeCommand
import plain.bookmaru.domain.event.port.`in`.command.EventCreateCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails
import java.time.LocalDateTime

data class EventWriteRequestDto(
    val title: String,
    val content: String,
    val imageUrl: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
) {
    fun toCreateCommand(principal: CustomUserDetails): EventCreateCommand {
        return EventCreateCommand(
            memberId = principal.id,
            title = title,
            content = content,
            imageUrl = imageUrl,
            startAt = startAt,
            endAt = endAt
        )
    }

    fun toChangeCommand(principal: CustomUserDetails, eventId: Long): EventChangeCommand {
        return EventChangeCommand(
            memberId = principal.id,
            eventId = eventId,
            title = title,
            content = content,
            imageUrl = imageUrl,
            startAt = startAt,
            endAt = endAt
        )
    }
}
