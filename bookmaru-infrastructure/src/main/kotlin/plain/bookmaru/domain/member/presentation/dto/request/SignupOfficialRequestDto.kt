package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.port.`in`.command.SignupOfficialCommand
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.member.vo.Profile

data class SignupOfficialRequestDto(
    val username: String,
    val password: String,
    val email: String? = null,
    val affiliationName: String,
    val verificationCode: String
) {
    fun toCommand(platformType: String) : SignupOfficialCommand {
        return SignupOfficialCommand(
            accountInfo = AccountInfo(this.username, this.password),
            affiliationName = this.affiliationName,
            profile = Profile(nickname = this.username),
            email = this.email?.ifBlank { null }.let { Email(it) },
            platformType = PlatformType.valueOf(platformType),
            verificationCode = this.verificationCode
        )
    }
}
