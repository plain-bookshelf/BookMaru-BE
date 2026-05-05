package plain.bookmaru.domain.member.port.out

import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadUrlResult

interface MemberProfileImageStoragePort {
    fun createPresignedUploadUrl(imageKey: String, contentType: String): ProfileImageUploadUrlResult
    fun delete(imageKey: String)
    fun toPublicUrl(imageKey: String): String
}
