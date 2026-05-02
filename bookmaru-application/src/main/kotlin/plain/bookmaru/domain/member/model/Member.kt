package plain.bookmaru.domain.member.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.OAuthInfo
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.member.vo.Profile
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.member.vo.LendingBook
import java.time.LocalTime

@Aggregate
class Member(
    val id: Long? = null,
    affiliationId: Long? = null,
    profile: Profile,
    val authority: Authority,
    accountInfo: AccountInfo? = null,
    email: Email,
    lendingBook: LendingBook,
    deleteStatus: Boolean = false,
    oAuthInfo: OAuthInfo? = null,
) {
    var accountInfo: AccountInfo? = accountInfo
        private set

    var profile: Profile = profile
        private set

    var oAuthInfo: OAuthInfo? = oAuthInfo
        private set

    var affiliationId: Long? = affiliationId
        private set

    var lendingBook: LendingBook = lendingBook
        private set

    var deleteStatus: Boolean = deleteStatus
        private set

    var email: Email = email
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
            email: Email,
            lending: LendingBook,
        ): Member {
            return Member(
                id = id,
                affiliationId = affiliationId,
                profile = profile,
                authority = authority,
                accountInfo = accountInfo,
                email = email,
                lendingBook = lending
            )
        }
        
        fun createOAuthMember(
            id : Long? = null,
            affiliationId: Long? = null,
            profile: Profile,
            email: Email,
            authority: Authority,
            oAuthInfo: OAuthInfo? = null,
            lendingBook: LendingBook
        ) : Member {
            return Member(
                id = id,
                affiliationId = affiliationId,
                profile = profile,
                authority = authority,
                accountInfo = null,
                email = email,
                oAuthInfo = oAuthInfo,
                lendingBook = lendingBook
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

    fun modifyUsername(newUsername: String) {
        this.accountInfo = accountInfo?.copy(username = newUsername)
    }

    fun modifyEmail(newEmail: String) {
        this.email = Email(newEmail)
    }

    fun modifyNickname(newNickname: String) {
        this.profile = profile.copy(nickname = newNickname)
    }

    fun modifyProfileImage(newProfileImageUrl: String) {
        this.profile = profile.copy(profileImage = newProfileImageUrl)
    }

    fun incrementRentalCount() {
        this.lendingBook = lendingBook.copy(rentalCount = this.lendingBook.rentalCount + 1)
    }

    fun decrementRentalCount() {
        this.lendingBook = lendingBook.copy(
            rentalCount = (this.lendingBook.rentalCount - 1).coerceAtLeast(0)
        )
    }

    fun incrementReservationCount() {
        this.lendingBook = lendingBook.copy(reservationCount = this.lendingBook.reservationCount + 1)
    }

    fun decrementReservationCount() {
        this.lendingBook = lendingBook.copy(
            reservationCount = (this.lendingBook.reservationCount - 1).coerceAtLeast(0)
        )
    }

    fun deleteStatus() {
        this.deleteStatus = true
    }
}
