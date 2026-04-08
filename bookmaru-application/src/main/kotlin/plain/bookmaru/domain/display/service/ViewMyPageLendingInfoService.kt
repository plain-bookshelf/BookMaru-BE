package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.`in`.ViewMyPageLendingInfoUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageLendingInfoCommand
import plain.bookmaru.domain.display.port.out.MyPagePort
import plain.bookmaru.domain.display.port.out.result.LendingBookListResult

private val log = KotlinLogging.logger {}

@Service
class ViewMyPageLendingInfoService(
    private val myPagePort: MyPagePort
): ViewMyPageLendingInfoUseCase {
    override suspend fun execute(command: ViewMyPageLendingInfoCommand): LendingBookListResult {
        val memberId = command.memberId
        val result = myPagePort.findUserLendingInfoByUsername(memberId)
        log.info { "memberId: $memberId 유저의 마이페이지 책 관련 정보를 가져왔습니다." }

        return result
    }

}