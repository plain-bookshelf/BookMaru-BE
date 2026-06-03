package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.auth.port.`in`.command.SocialSignupCommand

data class SocialSignupRequestDto(
    val affiliationName : String,
    val registerToken : String
) {
    fun toCommand(platformType: String) : SocialSignupCommand = SocialSignupCommand(
        affiliationName = affiliationName,
        registerToken = registerToken,
        platformType = platformType
    )
}
