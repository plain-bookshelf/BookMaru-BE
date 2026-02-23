package plain.bookmaru.domain.member.port.`in`.command

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.member.vo.Profile

data class SignupOfficialCommand(
    val accountInfo: AccountInfo,
    val affiliationName: String,
    val profile: Profile,
    val email: Email? = null,
    val platformType: PlatformType,
    val verificationCode: String
) {
    init {
        require(!accountInfo.username.contains('@')) { "유저 아이디는 @ 특수부호를 포함하면 안 됩니다." }
        require(affiliationName.length < 45) { "소속명 자릿수는 45자리 미만이어야 합니다. " }
        require(affiliationName.isNotBlank()) { "소속명은 비어있으면 안 됩니다." }
        require(accountInfo.password.length < 20) { "비밀번호 자릿수는 20자리 미만이어야 합니다." }
        require(platformType.name.length < 20) { "플랫폼 자릿수는 20자리 미만이어야 합니다." }
        require(accountInfo.password.first() in 'a'..'z' || accountInfo.password.first() in 'A'..'Z') { "비밀번호 첫째 자리는 영문자이어야 합니다." }
        require(accountInfo.password.last() in 'a'..'z' || accountInfo.password.last() in 'A'..'Z' || accountInfo.password.last() in '0'..'9' || accountInfo.password.last() == '!') { "비밀번호 마지막 자리는 영문자이어야 합니다." }
    }
}