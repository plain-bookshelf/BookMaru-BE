package plain.bookmaru.domain.event.presentation.dto.request

import plain.bookmaru.domain.event.port.`in`.command.EventCreateCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails
import java.time.LocalDateTime

data class EventCreateRequestDto(
    val title: String,
    val content: String,
    val imageUrl: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
) {
    fun toCommand(principal: CustomUserDetails): EventCreateCommand {
        return EventCreateCommand(
            memberId = principal.id,
            title = title,
            content = content,
            imageUrl = imageUrl,
            startAt = startAt,
            endAt = endAt
        )
    }
}
