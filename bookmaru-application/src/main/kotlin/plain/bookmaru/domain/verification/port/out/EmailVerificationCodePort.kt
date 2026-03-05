package plain.bookmaru.domain.verification.port.out

import plain.bookmaru.domain.verification.model.EmailVerification

interface EmailVerificationCodePort {
    suspend fun save(emailVerification: EmailVerification)
    suspend fun load(email: String) : EmailVerification?
    suspend fun delete(email: String)
}