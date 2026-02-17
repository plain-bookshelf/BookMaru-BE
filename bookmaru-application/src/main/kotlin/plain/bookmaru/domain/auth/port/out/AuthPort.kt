package plain.bookmaru.domain.auth.port.out

interface AuthPort {
    suspend fun save(accessToken: String, remainingTime: Long)
}