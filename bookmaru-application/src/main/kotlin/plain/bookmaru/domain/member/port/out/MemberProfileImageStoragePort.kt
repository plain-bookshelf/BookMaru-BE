package plain.bookmaru.domain.member.port.out

import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadUrlResult

interface MemberProfileImageStoragePort {
    fun createPresignedUploadUrl(imageKey: String, contentType: String): ProfileImageUploadUrlResult
    fun upload(imageKey: String, content: ByteArray, contentType: String)
    fun exists(imageKey: String): Boolean
    fun delete(imageKey: String)
    fun toPublicUrl(imageKey: String): String
}
