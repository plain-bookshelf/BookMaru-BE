package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.PlatformType

data class ReissueCommand(
    val refreshToken: String,
    val platformType: PlatformType
) {
    init {
        require(platformType.name.length < 20)
    }
}
