package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType

data class LoginMemberCommand(
    val accountInfo: AccountInfo,
    val platformType: PlatformType
) {
    init {
        requireNotNull(accountInfo.password) { "비밀번호 정보는 null 일 수 없습니다. 비정상적인 접근 입니다." }
        require(accountInfo.password.length <= 20 && accountInfo.password.length >= 8) { "비밀번호 자릿수는 8자 이상에서 20자 이하 이여야 합니다." }
        require(platformType.name.length < 20) { "Platform 정보는 20자 미만 이여야 합니다. 비정상적인 접근입니다." }
    }
}