package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.`in`.ViewMyPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageCommand
import plain.bookmaru.domain.display.port.out.MyPagePort
import plain.bookmaru.domain.display.port.out.result.ViewMyPageResult
import plain.bookmaru.domain.member.port.out.MemberProfileImageStoragePort

private val log = KotlinLogging.logger {}

@Service
class ViewMyPageService(
    private val myPagePort: MyPagePort,
    private val memberProfileImageStoragePort: MemberProfileImageStoragePort
): ViewMyPageUseCase {
    override suspend fun execute(command: ViewMyPageCommand): ViewMyPageResult {
        val username = command.username
        log.debug { "$username my_page 정보 조회 시도" }
        val myPageResult = myPagePort.findUserInfoByUsername(username)
        log.debug { "$username my_page 정보 조회 완료" }
        val profileImage = memberProfileImageStoragePort.toPublicUrl(myPageResult.profileImage)
        log.debug { "이미지 url 변환 성공" }
        return myPageResult.copy(profileImage = profileImage)
    }
}