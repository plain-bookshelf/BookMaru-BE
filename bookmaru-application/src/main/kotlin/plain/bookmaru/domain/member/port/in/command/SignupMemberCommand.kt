package plain.bookmaru.domain.member.port.`in`.command

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email

data class SignupMemberCommand(
    val accountInfo: AccountInfo,
    val affiliationName: String,
    val profile: Profile,
    val email: Email? = null,
    val platformType: PlatformType
)