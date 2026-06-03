package plain.bookmaru.domain.auth.persistent

import plain.bookmaru.domain.auth.vo.PlatformType

internal object RefreshTokenSessionKey {
    private const val NO_DEVICE = "NO_DEVICE"

    fun of(username: String, platformType: PlatformType, deviceToken: String?): String {
        val normalizedDeviceToken = deviceToken?.trim()?.takeIf { it.isNotEmpty() } ?: NO_DEVICE
        return "$username:$platformType:$normalizedDeviceToken"
    }
}
