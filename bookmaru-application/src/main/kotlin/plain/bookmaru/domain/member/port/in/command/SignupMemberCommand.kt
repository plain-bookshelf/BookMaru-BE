package plain.bookmaru.domain.member.port.`in`.command

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email

data class SignupMemberCommand(
    val accountInfo: AccountInfo,
    val affiliationName: String,
    val profile: Profile,
    val email: Email,
    val platformType: PlatformType
) {
    init {
        requireNotNull(accountInfo.password) { "비밀번호 정보는 null 일 수 없습니다. 비정상적인 접근 입니다." }

        val password = accountInfo.password
        val passwordFirst = password.first()
        val passwordLast = password.last()

        require(accountInfo.username.length < 255) { "유저 아이디 정보는 255자 미만이여야 합니다." }
        require(password.length <= 20 && password.length >= 8) { "비밀번호 자릿수는 8자 이상에서 20자 이하 이여야 합니다." }
        require(affiliationName.length < 45) { "소속명 자릿수는 45자리 미만이어야 합니다. " }
        require(affiliationName.isNotBlank()) { "소속명은 비어있으면 안 됩니다." }
        require(platformType.name.length < 20) { "플랫폼 자릿수는 20자리 미만이어야 합니다." }
        require(passwordFirst in 'a'..'z' || passwordFirst in 'A'..'Z') { "비밀번호 첫째 자리는 영문자이어야 합니다." }
        require(passwordLast in 'a'..'z' || passwordLast in 'A'..'Z' || passwordLast in '0'..'9' || passwordLast == '!' || passwordLast == '~' || passwordLast == '#') { "비밀번호 마지막 자리는 영문자, 숫자, !,~,# 중 하나 이어야 합니다." }
    }
}