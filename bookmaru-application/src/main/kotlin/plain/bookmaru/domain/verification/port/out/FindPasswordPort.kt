package plain.bookmaru.domain.verification.port.out

interface FindPasswordPort {
    suspend fun save(registerToken: String, username: String)
    suspend fun load(username: String) : String?
    suspend fun delete(username: String)
}