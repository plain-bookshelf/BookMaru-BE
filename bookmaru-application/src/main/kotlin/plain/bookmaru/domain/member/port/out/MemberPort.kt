package plain.bookmaru.domain.member.port.out

import plain.bookmaru.domain.member.model.Member

interface MemberPort {
    suspend fun findByUsername(username: String) : Member?
    suspend fun save(member: Member)
}