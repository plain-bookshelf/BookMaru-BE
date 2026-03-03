package plain.bookmaru.domain.auth.port.out

import plain.bookmaru.domain.auth.port.`in`.command.CustomOAuth2Command

interface OAuth2RegisterSessionPort {
    suspend fun save(token: String, command: CustomOAuth2Command)
    suspend fun getPendingUser(token: String) : CustomOAuth2Command?
    suspend fun removePendingUser(token: String)
}