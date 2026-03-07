package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand

interface ProfileImageChangeUseCase {
    suspend fun execute(command: ProfileImageChangeCommand)
}