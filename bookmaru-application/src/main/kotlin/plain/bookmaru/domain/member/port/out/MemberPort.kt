package plain.bookmaru.domain.member.port.out

import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult
import plain.bookmaru.domain.member.model.Member
import java.time.LocalDateTime

interface MemberPort {
    suspend fun findById(memberId: Long) : Member?
    suspend fun findByUsername(username: String) : Member?
    suspend fun findByEmail(email: String) : Member?
    suspend fun findAllByAffiliationId(affiliationId: Long): List<Member>
    suspend fun findUserRanking(affiliationId: Long): List<UserRankInfoResult>
    suspend fun validateNickname(nickname: String): Member?

    fun save(member: Member) : Member
    fun applyOverduePenalty(memberId: Long, overdueDays: Long)
    suspend fun markOverdueMembers(memberIds: Collection<Long>): Long
    suspend fun releaseExpiredOverduePenalties(now: LocalDateTime, activeOverdueMemberIds: Collection<Long>): Long

    suspend fun delete(member: Member)
}
