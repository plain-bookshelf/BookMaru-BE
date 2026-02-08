package plain.bookmaru.domain.verification.port.out

import plain.bookmaru.domain.verification.vo.EmailVerified

interface EmailVerifiedPort {
    suspend fun save(emailVerified: EmailVerified)
    suspend fun load(email: String) : EmailVerified?
}