package plain.bookmaru.domain.auth.presentation.dto.request

import plain.bookmaru.domain.auth.port.`in`.command.LoginMemberCommand
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.PlatformType

data class LoginMemberRequestDto(
    val username: String,
    val password: String
) {
    fun toCommand(platformType: String, deviceToken: String?): LoginMemberCommand {
        return LoginMemberCommand(
            accountInfo = AccountInfo(username = username, password = password),
            platformType = runCatching { PlatformType.valueOf(platformType) }.getOrDefault(PlatformType.WEB),
            deviceToken = deviceToken?.takeIf { it.isNotBlank() }
        )
    }
}
