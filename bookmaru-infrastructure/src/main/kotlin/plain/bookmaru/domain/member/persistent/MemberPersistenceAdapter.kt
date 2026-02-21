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

@Component
class MemberPersistenceAdapter(
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper
) : MemberPort {
    override suspend fun save(member: Member, affiliation: Affiliation) : Member = withContext(Dispatchers.IO) {
        val memberEntity = memberMapper.toEntity(member, affiliation)

        val savedEntity = memberRepository.save(memberEntity)
        return@withContext memberMapper.toDomain(savedEntity)
    }

    override suspend fun findByEmail(email: Email): Member? = withContext(Dispatchers.IO) {
        memberRepository.findByEmail(email)?.let {
            memberMapper.toDomain(it)
        }
    }

    override suspend fun findByUsername(username: String): Member? = withContext(Dispatchers.IO) {
        memberRepository.findByUsername(username)?.let {
            memberMapper.toDomain(it)
        }
    }
}