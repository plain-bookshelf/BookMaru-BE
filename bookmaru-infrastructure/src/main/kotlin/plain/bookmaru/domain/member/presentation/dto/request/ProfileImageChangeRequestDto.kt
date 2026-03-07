package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand

data class ProfileImageChangeRequestDto(
    val newProfileImageUrl: String
) {
    fun toCommand(username: String) : ProfileImageChangeCommand = ProfileImageChangeCommand(
        username,
        newProfileImageUrl
    )
}
