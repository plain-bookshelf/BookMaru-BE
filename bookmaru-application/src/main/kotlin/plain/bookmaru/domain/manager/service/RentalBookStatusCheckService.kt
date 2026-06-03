package plain.bookmaru.domain.manager.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.manager.port.`in`.RentalBookStatusCheckSearchMemberUseCase
import plain.bookmaru.domain.manager.port.`in`.RentalBookStatusCheckUseCase
import plain.bookmaru.domain.manager.port.`in`.command.RentalBookStatusCheckCommand
import plain.bookmaru.domain.manager.port.out.result.RentalBookStatusCheckResult

private val log = KotlinLogging.logger {}

@Service
class RentalBookStatusCheckService(
    private val bookDetailPort: BookDetailPort
): RentalBookStatusCheckUseCase, RentalBookStatusCheckSearchMemberUseCase {
    override suspend fun execute(command: RentalBookStatusCheckCommand): PageResult<RentalBookStatusCheckResult>? {
        log.debug { "대여된 책 상태 정보를 가져오기를 시도 했습니다." }
        val result = bookDetailPort.findRentalBookStatusCheckByAffiliationId(command.pageCommand, command.affiliationId)

        return result
    }

    override suspend fun searchMemberExecute(command: RentalBookStatusCheckCommand): PageResult<RentalBookStatusCheckResult>? {
        log.debug { "대여된 책 상태 정보에서 ${command.nickname} 유저 정보를 토대로 가져오기를 시도 했습니다." }
        val result = bookDetailPort.findRentalBookStatusCheckByAffiliationIdAndNickname(command.pageCommand, command.affiliationId, command.nickname!!)

        return result
    }
}