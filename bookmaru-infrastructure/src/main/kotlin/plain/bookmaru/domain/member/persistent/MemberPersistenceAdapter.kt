package plain.bookmaru.domain.member.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.repository.AffiliationRepository
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

        val memberEntity = memberMapper.toEntity(member, affiliationProxy)

        val savedEntity = memberRepository.save(memberEntity)
        return@withTransaction memberMapper.toDomain(savedEntity)
    }

    override suspend fun findByEmail(email: Email): Member? = dbProtection.withReadOnly {
        memberRepository.findByEmail(email)?.let {
            memberMapper.toDomain(it)
        }
    }

    override suspend fun findByUsername(username: String): Member? = dbProtection.withReadOnly {
        memberRepository.findByUsername(username)?.let {
            memberMapper.toDomain(it)
        }
    }
}