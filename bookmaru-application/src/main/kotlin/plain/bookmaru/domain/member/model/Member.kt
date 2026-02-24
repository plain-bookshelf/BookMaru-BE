package plain.bookmaru.domain.member.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email
import java.time.LocalDateTime

@Aggregate
class Member(
    val id: Long? = null,
    val affiliationId: Long,
    profile: Profile,
    val authority: Authority,
    accountInfo: AccountInfo,
    val email: Email?
) {
    var accountInfo: AccountInfo = accountInfo
        private set

    var profile: Profile = profile
        private set

    companion object {
        fun create(
            id : Long? = null,
            affiliationId: Long,
            profile: Profile,
            authority: Authority,
            accountInfo: AccountInfo,
            email: Email?
        ): Member {
            return Member(
                id = id,
                affiliationId = affiliationId,
                profile = profile,
                authority = authority,
                accountInfo = accountInfo,
                email = email
            )
        }
    }

    fun modifyPassword(newPassword: String) {
        this.accountInfo = accountInfo.copy(password = newPassword)
    }

    fun modifyOftenBookReadTime(time: LocalDateTime) {
        this.profile = profile.copy(oftenBookReadTime = time)
    }
}