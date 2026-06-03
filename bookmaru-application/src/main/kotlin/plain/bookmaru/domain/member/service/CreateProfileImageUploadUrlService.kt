package plain.bookmaru.domain.member.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.CreateProfileImageUploadUrlUseCase
import plain.bookmaru.domain.member.port.`in`.command.CreateProfileImageUploadUrlCommand
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.port.out.MemberProfileImageStoragePort
import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadUrlResult
import java.util.UUID

private const val MAX_PROFILE_IMAGE_SIZE_BYTES = 3L * 1024L * 1024L

@Service
class CreateProfileImageUploadUrlService(
    private val memberPort: MemberPort,
    private val memberProfileImageStoragePort: MemberProfileImageStoragePort
) : CreateProfileImageUploadUrlUseCase {

    override suspend fun execute(command: CreateProfileImageUploadUrlCommand): ProfileImageUploadUrlResult {
        val member = memberPort.findByUsername(command.username)
            ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
        val memberId = member.id ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")

        validateFileSize(command.fileSize)
        val extension = resolveExtension(command.fileName, command.contentType)
        val contentType = contentTypeFromExtension(extension)
        val imageKey = "members/$memberId/profile/${UUID.randomUUID()}.$extension"

        return memberProfileImageStoragePort.createPresignedUploadUrl(imageKey, contentType)
    }

    private fun validateFileSize(fileSize: Long) {
        require(fileSize in 1..MAX_PROFILE_IMAGE_SIZE_BYTES) {
            "프로필 이미지는 1바이트 이상 3MB 이하만 업로드할 수 있습니다."
        }
    }

    private fun resolveExtension(fileName: String, contentType: String): String {
        val extension = normalizeExtension(fileName.substringAfterLast('.', ""))
            .lowercase()
            .takeIf { it.isNotBlank() }
            ?: extensionFromContentType(contentType)

        require(extension in ALLOWED_EXTENSIONS) {
            "지원하지 않는 이미지 확장자입니다."
        }

        val resolvedContentType = contentTypeFromExtension(extension)

        require(resolvedContentType in ALLOWED_CONTENT_TYPES) {
            "지원하지 않는 이미지 타입입니다."
        }

        require(extension == extensionFromContentType(resolvedContentType)) {
            "파일 확장자와 이미지 타입이 일치하지 않습니다."
        }

        return extension
    }

    private fun extensionFromContentType(contentType: String): String {
        return when (contentType.lowercase()) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> ""
        }
    }

    private fun contentTypeFromExtension(extension: String): String {
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> ""
        }
    }

    private fun normalizeExtension(extension: String): String {
        return when (extension.lowercase()) {
            "jpeg" -> "jpg"
            else -> extension
        }
    }

    companion object {
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
}
