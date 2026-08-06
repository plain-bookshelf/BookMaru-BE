package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand
import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadResult

interface ProfileImageChangeUseCase {
    suspend fun execute(command: ProfileImageChangeCommand): ProfileImageUploadResult
}
