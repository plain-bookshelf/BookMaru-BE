package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.member.port.`in`.command.OftenReadBookTimeSetCommand
import java.time.LocalDateTime

data class OftenReadBookTimeRequestDto(
    val time: LocalDateTime
) {
    fun toCommand(username : String) : OftenReadBookTimeSetCommand = OftenReadBookTimeSetCommand(time = time, username = username)
}