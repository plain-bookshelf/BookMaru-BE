package plain.bookmaru.domain.member.port.out.result

data class ProfileImageUploadResult(
    val imageKey: String,
    val publicUrl: String
)
