package plain.bookmaru.domain.member.port.out

import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult
import plain.bookmaru.domain.member.model.Member

interface MemberPort {
    suspend fun findByUsername(username: String) : Member?
    suspend fun findByEmail(email: String) : Member?
    suspend fun findUserRanking(affiliationId: Long): List<UserRankInfoResult>

    fun save(member: Member) : Member

    suspend fun delete(member: Member)
}