package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand

data class ProfileImageChangeRequestDto(
    val imageKey: String
) {
    fun toCommand(username: String): ProfileImageChangeCommand {
        return ProfileImageChangeCommand(
            username = username,
            imageKey = imageKey
        )
    }
}
