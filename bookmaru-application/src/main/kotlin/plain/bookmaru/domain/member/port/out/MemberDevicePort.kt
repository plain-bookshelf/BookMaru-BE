package plain.bookmaru.domain.member.port.out

import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.model.MemberDevice

interface MemberDevicePort {
    suspend fun upsert(memberId: Long, deviceToken: String, platformType: PlatformType)
    suspend fun findAllByMemberId(memberId: Long): List<MemberDevice>
    suspend fun deleteByMemberId(memberId: Long, deviceToken: String)
    suspend fun deleteByMemberUsername(username: String, deviceToken: String)
    suspend fun deleteAllByMemberId(memberId: Long)
}
