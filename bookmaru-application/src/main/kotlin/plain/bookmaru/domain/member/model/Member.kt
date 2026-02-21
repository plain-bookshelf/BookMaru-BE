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
    val profile: Profile?,
    val authority: Authority,
    val accountInfo: AccountInfo,
    val email: Email?
) {
    companion object {
        fun create(
            id : Long? = null,
            affiliationId: Long,
            profile: Profile?,
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
}