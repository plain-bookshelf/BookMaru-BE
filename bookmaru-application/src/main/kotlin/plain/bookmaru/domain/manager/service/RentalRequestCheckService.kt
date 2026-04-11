package plain.bookmaru.domain.manager.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.manager.port.`in`.RentalRequestCheckUseCase
import plain.bookmaru.domain.manager.port.`in`.command.RentalRequestCheckCommand
import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult

private val log = KotlinLogging.logger {}

@Service
class RentalRequestCheckService(
    private val bookRentalRecordPort: BookRentalRecordPort
) : RentalRequestCheckUseCase {
    override suspend fun execute(command: RentalRequestCheckCommand): List<RentalRequestCheckResult>? {
        val affiliationId = command.affiliationId
        log.debug { "affiliationId: $affiliationId 소속의 대여 요청 정보를 불러오기를 시도 했습니다." }
        val result = bookRentalRecordPort.findRentalRequestBookByAffiliationId(command.affiliationId)

        return result
    }
}