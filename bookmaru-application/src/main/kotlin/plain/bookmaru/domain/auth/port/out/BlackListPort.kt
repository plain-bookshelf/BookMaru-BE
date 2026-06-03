package plain.bookmaru.domain.auth.port.out

interface BlackListPort {
    suspend fun save(accessToken: String, remainingTime: Long)
}