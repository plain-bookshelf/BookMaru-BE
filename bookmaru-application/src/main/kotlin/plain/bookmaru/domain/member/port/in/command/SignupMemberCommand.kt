package plain.bookmaru.domain.member.port.`in`.command

import plain.bookmaru.domain.affiliation.vo.Affiliation
import plain.bookmaru.domain.auth.model.AccountInfo
import plain.bookmaru.domain.auth.model.Authority
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email

data class SignupMemberCommand(
    val accountInfo: AccountInfo,
    val authority: Authority,
    val affiliationName: String,
    val profile: Profile,
    val email: Email? = null
)