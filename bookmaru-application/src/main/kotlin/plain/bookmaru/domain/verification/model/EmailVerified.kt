package plain.bookmaru.domain.verification.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.member.vo.Email
import java.time.Instant

@Aggregate
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