package plain.bookmaru.domain.member.port.out

interface MemberProfileImageStoragePort {
    fun upload(imageKey: String, content: ByteArray, contentType: String)
    fun delete(imageKey: String)
    fun toPublicUrl(imageKey: String): String
}
