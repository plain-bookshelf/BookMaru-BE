package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.CreateProfileImageUploadUrlCommand
import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadUrlResult

interface CreateProfileImageUploadUrlUseCase {
    suspend fun execute(command: CreateProfileImageUploadUrlCommand): ProfileImageUploadUrlResult
}
