package plain.bookmaru.domain.display.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.`in`.ViewMyPageLikeBookUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageLikeBookCommand
import plain.bookmaru.domain.display.port.out.MyPagePort
import plain.bookmaru.domain.display.port.out.result.ViewMyPageLikeBookResult

@Service
class ViewMyPageLikeBookService(
    private val myPagePort: MyPagePort
): ViewMyPageLikeBookUseCase {
    override suspend fun execute(command: ViewMyPageLikeBookCommand): List<ViewMyPageLikeBookResult> {
        val memberId = command.memberId

        val result = myPagePort.findLikeBookByMemberId(memberId)

        return result
    }
}