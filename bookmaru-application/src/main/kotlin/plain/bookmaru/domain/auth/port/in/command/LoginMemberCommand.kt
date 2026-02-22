package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType

data class LoginMemberCommand(
    val accountInfo: AccountInfo,
    val platformType: PlatformType
) {
    init {
        require(!accountInfo.username.contains('@'))
        require(accountInfo.password.length < 20)
        require(platformType.name.length < 20)
    }
}