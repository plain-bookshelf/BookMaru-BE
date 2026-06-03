package plain.bookmaru.domain.member.port.out.result

import java.time.Instant

data class ProfileImageUploadUrlResult(
    val uploadUrl: String,
    val imageKey: String,
    val publicUrl: String,
    val expiresAt: Instant
)
