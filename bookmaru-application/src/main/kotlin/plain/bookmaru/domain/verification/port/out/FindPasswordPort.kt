package plain.bookmaru.domain.verification.port.out

interface FindPasswordPort {
    suspend fun save(uuid: String, email: String)
    suspend fun load(email: String): String?
    suspend fun delete(email: String)
}