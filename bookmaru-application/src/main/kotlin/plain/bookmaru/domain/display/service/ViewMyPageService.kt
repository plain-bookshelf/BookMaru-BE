package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.`in`.ViewMyPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageCommand
import plain.bookmaru.domain.display.port.out.MyPagePort
import plain.bookmaru.domain.display.port.out.result.ViewMyPageResult

private val log = KotlinLogging.logger {}

@Service
class ViewMyPageService(
    private val myPagePort: MyPagePort
): ViewMyPageUseCase {
    override suspend fun execute(command: ViewMyPageCommand): ViewMyPageResult {
        val username = command.username
        log.info { "$username my_page 정보 조회 시도" }
        val myPageResult = myPagePort.findUserInfoByUsername(username)
        log.info { "$username my_page 정보 조회 완료" }
        return myPageResult
    }
}