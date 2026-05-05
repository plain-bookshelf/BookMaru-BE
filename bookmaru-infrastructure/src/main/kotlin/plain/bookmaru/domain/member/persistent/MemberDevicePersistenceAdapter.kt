package plain.bookmaru.domain.member.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.persistent.mapper.MemberDeviceMapper
import plain.bookmaru.domain.member.persistent.repository.MemberDeviceRepository
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.domain.member.port.out.MemberDevicePort
import plain.bookmaru.global.config.DbProtection

@Component
class MemberDevicePersistenceAdapter(
    private val memberDeviceRepository: MemberDeviceRepository,
    private val memberRepository: MemberRepository,
    private val memberDeviceMapper: MemberDeviceMapper,
    private val dbProtection: DbProtection
) : MemberDevicePort {

    override suspend fun upsert(memberId: Long, deviceToken: String, platformType: PlatformType) = dbProtection.withTransaction {
        val normalizedToken = deviceToken.trim()
        val existingDevice = memberDeviceRepository.findByDeviceToken(normalizedToken)

        if (existingDevice == null) {
            val memberReference = memberRepository.getReferenceById(memberId)
            memberDeviceRepository.save(
                memberDeviceMapper.toEntity(memberReference, normalizedToken, platformType)
            )
            return@withTransaction
        }

        if (existingDevice.memberEntity.id != memberId || existingDevice.platformType != platformType) {
            existingDevice.memberEntity = memberRepository.getReferenceById(memberId)
            existingDevice.platformType = platformType
            memberDeviceRepository.save(existingDevice)
        }
    }

    override suspend fun findAllByMemberId(memberId: Long) = dbProtection.withReadOnly {
        memberDeviceRepository.findAllByMemberEntityId(memberId).map(memberDeviceMapper::toDomain)
    }

    override suspend fun deleteByMemberId(memberId: Long, deviceToken: String) = dbProtection.withTransaction {
        memberDeviceRepository.deleteByMemberEntityIdAndDeviceToken(memberId, deviceToken.trim())
    }

    override suspend fun deleteByMemberUsername(username: String, deviceToken: String) = dbProtection.withTransaction {
        val member = memberRepository.findByUsername(username) ?: return@withTransaction
        memberDeviceRepository.deleteByMemberEntityIdAndDeviceToken(member.id!!, deviceToken.trim())
    }

    override suspend fun deleteAllByMemberId(memberId: Long) = dbProtection.withTransaction {
        memberDeviceRepository.deleteAllByMemberEntityId(memberId)
    }
}
