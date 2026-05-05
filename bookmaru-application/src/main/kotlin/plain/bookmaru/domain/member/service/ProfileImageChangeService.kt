package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.ProfileImageChangeUseCase
import plain.bookmaru.domain.member.port.`in`.UploadProfileImageUseCase
import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand
import plain.bookmaru.domain.member.port.`in`.command.UploadProfileImageCommand
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
) : ProfileImageChangeUseCase, UploadProfileImageUseCase {

    companion object {
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }

    override suspend fun execute(command: ProfileImageChangeCommand) {
        val member = memberPort.findByUsername(command.username)
            ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
        val memberId = member.id ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
        val imageKey = command.imageKey.trim()
        val previousImageKey = member.profile.profileImage

        validateImageKey(memberId, imageKey)
        validateImageUploaded(imageKey)

        member.modifyProfileImage(imageKey)
        transactionPort.withTransaction {
            memberPort.save(member)
        }

        deletePreviousProfileImage(previousImageKey, imageKey, memberId)

        log.info { "프로필 이미지 정보를 변경했습니다. memberId=$memberId" }
    }

    override suspend fun execute(command: UploadProfileImageCommand): ProfileImageUploadResult {
        val member = memberPort.findByUsername(command.username)
            ?: throw NotFoundMemberException("?ъ슜?먮? 李얠쓣 ???놁뒿?덈떎.")
        val memberId = member.id ?: throw NotFoundMemberException("?ъ슜?먮? 李얠쓣 ???놁뒿?덈떎.")
        val previousImageKey = member.profile.profileImage

        validateFileSize(command.fileSize)
        val contentType = resolveContentType(command.fileName, command.contentType)
        val extension = extensionFromContentType(contentType)
        val imageKey = "members/$memberId/profile/${UUID.randomUUID()}.$extension"

        memberProfileImageStoragePort.upload(imageKey, command.content, contentType)

        member.modifyProfileImage(imageKey)
        transactionPort.withTransaction {
            memberPort.save(member)
        }

        deletePreviousProfileImage(previousImageKey, imageKey, memberId)

        log.info { "Profile image uploaded and changed. memberId=$memberId" }

        return ProfileImageUploadResult(
            imageKey = imageKey,
            publicUrl = memberProfileImageStoragePort.toPublicUrl(imageKey)
        )
    }

    private fun validateImageKey(memberId: Long, imageKey: String) {
        require(imageKey.startsWith("members/$memberId/profile/")) {
            "프로필 이미지 경로가 올바르지 않습니다."
        }

        val extension = imageKey.substringAfterLast('.', "").lowercase()
        require(extension in ALLOWED_EXTENSIONS) {
            "지원하지 않는 프로필 이미지 형식입니다."
        }
    }

    private fun validateImageUploaded(imageKey: String) {
        require(memberProfileImageStoragePort.exists(imageKey)) {
            "Profile image object does not exist in S3. imageKey=$imageKey"
        }
    }

    private fun validateFileSize(fileSize: Long) {
        require(fileSize in 1..MAX_PROFILE_IMAGE_SIZE_BYTES) {
            "Profile image must be greater than 0 bytes and less than or equal to 3MB."
        }
    }

    private fun resolveContentType(fileName: String, contentType: String): String {
        val normalizedContentType = contentType.lowercase()
        if (normalizedContentType in ALLOWED_CONTENT_TYPES) {
            return normalizedContentType
        }

        val extension = normalizeExtension(fileName.substringAfterLast('.', ""))
            .lowercase()
            .takeIf { it.isNotBlank() }
            ?: ""

        require(extension in ALLOWED_EXTENSIONS) {
            "Unsupported profile image extension."
        }

        return contentTypeFromExtension(extension)
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
