package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand

data class  SignupRequestDto(
    val username: String,
    val password: String,
    val email: String? = null,
    val affiliationName: String
) {
    fun toCommand(platformType: String): SignupMemberCommand {
        return SignupMemberCommand(
            email = email?.ifBlank { null }?.let { Email(it) },
            accountInfo = AccountInfo(this.username, this.password),
            affiliationName = this.affiliationName,
            profile = Profile(nickname = this.username),
            authority = Authority.ROLE_USER,
            platformType = PlatformType.valueOf(platformType)
        )
    }
}