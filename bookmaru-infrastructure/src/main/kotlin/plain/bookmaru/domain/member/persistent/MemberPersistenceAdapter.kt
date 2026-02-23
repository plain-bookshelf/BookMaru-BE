package plain.bookmaru.domain.member.persistent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.persistent.mapper.MemberMapper
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.global.config.DbProtection

@Component
class MemberPersistenceAdapter(
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper,
    private val dbProtection: DbProtection
) : MemberPort {
    override suspend fun save(member: Member, affiliation: Affiliation) : Member = dbProtection.withTransaction {
        val memberEntity = memberMapper.toEntity(member, affiliation)

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