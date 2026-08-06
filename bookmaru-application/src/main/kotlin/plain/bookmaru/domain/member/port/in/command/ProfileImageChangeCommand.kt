package plain.bookmaru.domain.member.port.`in`.command

data class ProfileImageChangeCommand(
    val username: String,
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ProfileImageChangeCommand
        return username == other.username &&
            fileName == other.fileName &&
            contentType == other.contentType &&
            fileSize == other.fileSize &&
            content.contentEquals(other.content)
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
