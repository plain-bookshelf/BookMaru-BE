package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.member.port.`in`.command.CreateProfileImageUploadUrlCommand

data class ProfileImageUploadUrlRequestDto(
    val fileName: String,
    val contentType: String,
    val fileSize: Long
) {
    fun toCommand(username: String): CreateProfileImageUploadUrlCommand {
        return CreateProfileImageUploadUrlCommand(
            username = username,
            fileName = fileName,
            contentType = contentType,
            fileSize = fileSize
        )
    }
}
