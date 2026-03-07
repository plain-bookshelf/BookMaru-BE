package plain.bookmaru.domain.member.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.OAuthInfo
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email
import java.time.LocalTime

@Aggregate
class Member(
    val id: Long? = null,
    affiliationId: Long? = null,
    profile: Profile,
    val authority: Authority,
    accountInfo: AccountInfo? = null,
    val email: Email?,
    oAuthInfo: OAuthInfo? = null
) {
    var accountInfo: AccountInfo? = accountInfo
        private set

    var profile: Profile = profile
        private set

    var oAuthInfo: OAuthInfo? = oAuthInfo
        private set

    var affiliationId: Long? = affiliationId
        private set

    fun linkOAuthAccount(provider: OAuthProvider, providerId: String) {
        this.oAuthInfo = OAuthInfo(provider, providerId)
    }

    companion object {
        fun createMember(
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
        
        fun createOAuthMember(
            id : Long? = null,
            affiliationId: Long? = null,
            profile: Profile,
            email: Email?,
            authority: Authority,
            oAuthInfo: OAuthInfo? = null
        ) : Member {
            return Member(
                id = id,
                affiliationId = affiliationId,
                profile = profile,
                authority = authority,
                accountInfo = null,
                email = email,
                oAuthInfo = oAuthInfo
            )
        }
    }

    fun modifyPassword(newPassword: String) {
        this.accountInfo = accountInfo?.copy(password = newPassword)
    }

    fun modifyOftenBookReadTime(time: LocalTime) {
        this.profile = profile.copy(oftenBookReadTime = time)
    }

    fun modifyAffiliation(affiliationId: Long) {
        this.affiliationId = affiliationId
    }

    fun modifyNickname(newNickname: String) {
        this.profile = profile.copy(nickname = newNickname)
    }
}