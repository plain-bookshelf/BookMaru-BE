package plain.bookmaru.domain.member.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.member.persistent.entity.MemberDeviceEntity

interface MemberDeviceRepository : JpaRepository<MemberDeviceEntity, Long> {
    fun findByDeviceToken(deviceToken: String): MemberDeviceEntity?
    fun findAllByMemberEntityId(memberId: Long): List<MemberDeviceEntity>
    fun deleteByMemberEntityIdAndDeviceToken(memberId: Long, deviceToken: String)
    fun deleteAllByMemberEntityId(memberId: Long)
}
