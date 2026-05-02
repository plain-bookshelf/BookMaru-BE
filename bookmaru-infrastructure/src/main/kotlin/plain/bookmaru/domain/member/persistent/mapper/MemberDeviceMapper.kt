package plain.bookmaru.domain.member.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.model.MemberDevice
import plain.bookmaru.domain.member.persistent.entity.MemberDeviceEntity
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.member.vo.DeviceInfo

@Component
class MemberDeviceMapper {

    fun toDomain(entity: MemberDeviceEntity): MemberDevice {
        return MemberDevice(
            id = entity.id ?: 0L,
            memberId = entity.memberEntity.id ?: 0L,
            deviceInfo = DeviceInfo(
                deviceToken = entity.deviceToken,
                platformType = entity.platformType
            )
        )
    }

    fun toEntity(memberEntity: MemberEntity, deviceToken: String, platformType: PlatformType): MemberDeviceEntity {
        return MemberDeviceEntity(
            memberEntity = memberEntity,
            deviceToken = deviceToken,
            platformType = platformType
        )
    }
}
