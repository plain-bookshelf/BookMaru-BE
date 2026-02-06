package plain.bookmaru.domain.verificationcode.port.out

import plain.bookmaru.domain.verificationcode.model.EmailVerification

interface EmailVerificationRepositoryPort {
    suspend fun save(emailVerification: EmailVerification)
    suspend fun load(email: String) : EmailVerification?
}