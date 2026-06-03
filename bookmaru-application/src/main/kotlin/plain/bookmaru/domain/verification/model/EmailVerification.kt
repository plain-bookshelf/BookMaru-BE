package plain.bookmaru.domain.verification.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.vo.VerificationCodeType
import plain.bookmaru.domain.verification.vo.VerificationData
import java.time.Instant
import kotlin.random.Random

@Aggregate
data class EmailVerification(
    val email: Email,
    val codeData: VerificationData,
    val expiredAt: Instant
) {
    companion object {
        fun create(email: Email, codeType: VerificationCodeType) : EmailVerification {
            val randomCode = generateRandomCode()
            return EmailVerification(
                email = email,
                codeData = VerificationData(randomCode, codeType),
                expiredAt = Instant.now().plusSeconds(300)
            )
        }

        private fun generateRandomCode() : String {
            val charPool : List<Char> = ('A'..'Z') + ('0'..'9')

            return (1..6)
                .map { Random.Default.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }
    }
}