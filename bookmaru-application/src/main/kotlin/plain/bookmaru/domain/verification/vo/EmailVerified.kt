package plain.bookmaru.domain.verification.vo

import plain.bookmaru.domain.member.vo.Email
import java.time.Instant

data class EmailVerified(
    val email: Email,
    val value: String,
    val expiredAt: Instant
) {
    companion object {
        fun create(email: Email) : EmailVerified {
            return EmailVerified(
                email = email,
                value = "True",
                expiredAt = Instant.now().plusSeconds(600)
            )
        }
    }
}