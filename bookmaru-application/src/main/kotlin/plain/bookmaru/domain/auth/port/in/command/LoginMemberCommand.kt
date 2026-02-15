package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType

data class LoginMemberCommand(
    val accountInfo: AccountInfo,
    val platformType: PlatformType
) {

}