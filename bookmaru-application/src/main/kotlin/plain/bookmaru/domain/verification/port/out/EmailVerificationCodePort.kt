package plain.bookmaru.domain.verification.port.out

import plain.bookmaru.domain.verification.vo.EmailVerification

interface EmailVerificationCodePort {
    suspend fun save(emailVerification: EmailVerification)
    suspend fun load(email: String) : EmailVerification?
}