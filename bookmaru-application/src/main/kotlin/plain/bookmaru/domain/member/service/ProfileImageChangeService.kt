package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.ProfileImageChangeUseCase
import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.port.out.MemberProfileImageStoragePort
import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadResult
import java.util.UUID

private val log = KotlinLogging.logger {}
private const val MAX_PROFILE_IMAGE_SIZE_BYTES = 3L * 1024L * 1024L

@Service
class ProfileImageChangeService(
    private val memberPort: MemberPort,
    private val memberProfileImageStoragePort: MemberProfileImageStoragePort,
    private val transactionPort: TransactionPort
) : ProfileImageChangeUseCase {

    companion object {
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }

    override suspend fun execute(command: ProfileImageChangeCommand): ProfileImageUploadResult {
        val member = memberPort.findByUsername(command.username)
            ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
        val memberId = member.id ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
        val previousImageKey = member.profile.profileImage

        validateFileSize(command.fileSize)
        val extension = resolveExtension(command.fileName, command.contentType)
        val contentType = contentTypeFromExtension(extension)
        val imageKey = "members/$memberId/profile/${UUID.randomUUID()}.$extension"

        memberProfileImageStoragePort.upload(imageKey, command.content, contentType)

        member.modifyProfileImage(imageKey)
        transactionPort.withTransaction {
            memberPort.save(member)
        }

        deletePreviousProfileImage(previousImageKey, imageKey, memberId)

        log.info { "프로필 이미지를 변경했습니다. memberId=$memberId" }

        return ProfileImageUploadResult(
            imageKey = imageKey,
            publicUrl = memberProfileImageStoragePort.toPublicUrl(imageKey)
        )
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

    private fun deletePreviousProfileImage(previousImageKey: String?, currentImageKey: String, memberId: Long) {
        if (previousImageKey.isNullOrBlank() || previousImageKey == currentImageKey) return
        if (!previousImageKey.startsWith("members/$memberId/profile/")) return

        runCatching {
            memberProfileImageStoragePort.delete(previousImageKey)
        }.onFailure {
            log.warn(it) { "기존 프로필 이미지 삭제에 실패했습니다. memberId=$memberId" }
        }
    }
}
