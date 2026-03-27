package plain.bookmaru.domain.member.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.repository.AffiliationRepository
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.persistent.mapper.MemberMapper
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.global.config.DbProtection

@Component
class MemberPersistenceAdapter(
    private val affiliationRepository : AffiliationRepository,
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper,
    private val dbProtection: DbProtection
) : MemberPort {
    override suspend fun save(member: Member) : Member = dbProtection.withTransaction {
        val affiliationProxy = affiliationRepository.getReferenceById(member.affiliationId!!)

        if (member.id == null) {
            val memberEntity = memberMapper.toEntity(member, affiliationProxy)
            val saved = memberRepository.save(memberEntity)
            return@withTransaction memberMapper.toDomain(saved)
        } else {
            val existingEntity = memberRepository.findById(member.id!!)
                .orElseThrow { throw NotFoundMemberException("존재하지 않는 유저입니다.") }

            memberMapper.updateEntity(member, existingEntity, affiliationProxy)

            return@withTransaction memberMapper.toDomain(existingEntity)
        }
    }

    override suspend fun findByEmail(email: String): Member? = dbProtection.withReadOnly {
        memberRepository.findByEmail(email)?.let {
            memberMapper.toDomain(it)
        }
    }

    override suspend fun delete(member: Member) = dbProtection.withTransaction {
        val affiliationProxy = affiliationRepository.getReferenceById(member.affiliationId!!)

        memberRepository.delete(memberMapper.toEntity(member, affiliationProxy))
    }

    override suspend fun findByUsername(username: String): Member? = dbProtection.withReadOnly {
        memberRepository.findByUsername(username)?.let {
            memberMapper.toDomain(it)
        }
    }
}