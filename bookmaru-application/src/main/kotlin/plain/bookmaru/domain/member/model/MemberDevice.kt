package plain.bookmaru.domain.member.model

import plain.bookmaru.domain.member.vo.DeviceInfo

class MemberDevice(
    val id: Long,
    val memberId: Long,
    val deviceInfo: DeviceInfo
)