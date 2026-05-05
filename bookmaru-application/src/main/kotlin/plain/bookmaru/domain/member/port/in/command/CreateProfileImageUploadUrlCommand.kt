package plain.bookmaru.domain.member.port.`in`.command

data class CreateProfileImageUploadUrlCommand(
    val username: String,
    val fileName: String,
    val contentType: String,
    val fileSize: Long
)
