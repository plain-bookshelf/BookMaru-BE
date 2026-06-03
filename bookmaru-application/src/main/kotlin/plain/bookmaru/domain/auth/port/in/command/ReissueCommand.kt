package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.PlatformType

data class ReissueCommand(
    val refreshToken: String,
    val platformType: PlatformType,
    val deviceToken: String? = null
) {
    init {
        require(platformType.name.length < 20)
        require(deviceToken == null || deviceToken.length <= 255)
    }
}
