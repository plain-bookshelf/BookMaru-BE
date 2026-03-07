package plain.bookmaru.domain.member.port.out

import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.vo.Email

interface MemberPort {
    suspend fun findByUsername(username: String) : Member?
    suspend fun save(member: Member) : Member
    suspend fun findByEmail(email: Email) : Member?
    suspend fun delete(member: Member)
}