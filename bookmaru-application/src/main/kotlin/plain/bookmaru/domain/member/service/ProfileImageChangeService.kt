package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.ProfileImageChangeUseCase
import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.port.out.MemberProfileImageStoragePort

private val log = KotlinLogging.logger {}

@Service
class ProfileImageChangeService(
    private val memberPort: MemberPort,
    private val memberProfileImageStoragePort: MemberProfileImageStoragePort,
    private val transactionPort: TransactionPort
) : ProfileImageChangeUseCase {
    override suspend fun execute(command: ProfileImageChangeCommand) {
        val member = memberPort.findByUsername(command.username)
            ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
        val memberId = member.id ?: throw NotFoundMemberException("사용자를 찾을 수 없습니다.")
        val imageKey = command.imageKey.trim()
        val previousImageKey = member.profile.profileImage

        validateImageKey(memberId, imageKey)

        member.modifyProfileImage(imageKey)
        transactionPort.withTransaction {
            memberPort.save(member)
        }

        deletePreviousProfileImage(previousImageKey, imageKey, memberId)

        log.info { "프로필 이미지 정보를 변경했습니다. memberId=$memberId" }
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

    private fun deletePreviousProfileImage(previousImageKey: String?, currentImageKey: String, memberId: Long) {
        if (previousImageKey.isNullOrBlank() || previousImageKey == currentImageKey) return
        if (!previousImageKey.startsWith("members/$memberId/profile/")) return

        runCatching {
            memberProfileImageStoragePort.delete(previousImageKey)
        }.onFailure {
            log.warn(it) { "기존 프로필 이미지 삭제에 실패했습니다. memberId=$memberId" }
        }
    }

    companion object {
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
    }
}
