package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.auth.port.`in`.command.CustomOAuth2Command
import plain.bookmaru.domain.auth.port.out.result.LoginResult

interface CustomOAuth2UseCase {
    suspend fun execute(command: CustomOAuth2Command) : LoginResult
}