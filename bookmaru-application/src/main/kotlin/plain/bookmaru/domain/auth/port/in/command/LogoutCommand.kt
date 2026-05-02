package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.PlatformType

data class LogoutCommand(
    val accessToken: String,
    val username: String,
    val platformType: PlatformType,
    val deviceToken: String? = null
)
