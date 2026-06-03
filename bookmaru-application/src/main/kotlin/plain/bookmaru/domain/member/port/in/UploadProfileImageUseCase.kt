package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.UploadProfileImageCommand
import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadResult

interface UploadProfileImageUseCase {
    suspend fun execute(command: UploadProfileImageCommand): ProfileImageUploadResult
}
