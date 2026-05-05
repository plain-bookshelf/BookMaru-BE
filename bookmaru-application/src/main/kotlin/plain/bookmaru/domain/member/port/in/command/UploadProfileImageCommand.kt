package plain.bookmaru.domain.member.port.`in`.command

data class UploadProfileImageCommand(
    val username: String,
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadProfileImageCommand

        if (username != other.username) return false
        if (fileName != other.fileName) return false
        if (contentType != other.contentType) return false
        if (fileSize != other.fileSize) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + fileSize.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
