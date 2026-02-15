package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.PlatformType

data class ReissueCommand(
    val refreshToken: String,
    val platformType: PlatformType
) {
    companion object {
        fun toCommand(refreshToken: String, platformType: String) : ReissueCommand {
            return ReissueCommand(
                refreshToken = refreshToken,
                platformType = PlatformType.valueOf(platformType)
            )
        }
    }
}
