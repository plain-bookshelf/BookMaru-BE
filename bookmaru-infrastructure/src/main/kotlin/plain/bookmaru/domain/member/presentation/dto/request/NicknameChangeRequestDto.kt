package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.member.port.`in`.command.NicknameChangeCommand

data class NicknameChangeRequestDto(
    val newNickname: String
) {
    fun toCommand(username: String): NicknameChangeCommand = NicknameChangeCommand(newNickname, username)
}
