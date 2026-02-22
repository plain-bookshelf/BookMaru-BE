package plain.bookmaru.domain.member.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email

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

    fun retouchPassword(newPassword: String) {
         this.accountInfo = AccountInfo(
            username = this.accountInfo.username,
            password = newPassword
        )
    }
}