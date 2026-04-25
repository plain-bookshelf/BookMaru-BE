package plain.bookmaru.domain.member.vo

import plain.bookmaru.domain.auth.vo.PlatformType

data class DeviceInfo(
    val deviceToken: String,
    val platformType: PlatformType
)