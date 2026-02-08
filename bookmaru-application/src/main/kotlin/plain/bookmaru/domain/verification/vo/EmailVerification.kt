package plain.bookmaru.domain.verification.vo

import plain.bookmaru.domain.member.vo.Email
import java.time.Instant
import kotlin.random.Random

data class EmailVerification(
    val email: Email,
    val code: String,
    val expiredAt: Instant
) {
    companion object {
        fun create(email: Email) : EmailVerification {
            val randomCode = generateRandomAlphanumeric()
            return EmailVerification(
                email = email,
                code = randomCode,
                expiredAt = Instant.now().plusSeconds(300)
            )
        }

        private fun generateRandomAlphanumeric() : String {
            val charPool : List<Char> = ('A'..'Z') + ('0'..'9')

            return (1..6)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }
    }
}