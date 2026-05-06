package plain.bookmaru.domain.lending.port.out

import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.port.out.result.OverdueNotificationTarget
import plain.bookmaru.domain.lending.port.out.result.RentalRequestApprovalInfo
import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult
import plain.bookmaru.domain.auth.vo.PlatformType
import java.time.LocalDate

interface BookRentalRecordPort {
    fun save(renter: Rental)
    fun completeReturn(bookDetailId: Long)
    suspend fun findRentalRequestBookByAffiliationId(affiliationId: Long) : List<RentalRequestCheckResult>?
    suspend fun findRentalRequestBookByAffiliationIdForPlatform(affiliationId: Long, platformType: PlatformType) : List<RentalRequestCheckResult>?
    suspend fun findRentalRequestApprovalInfo(bookDetailId: Long, affiliationId: Long): RentalRequestApprovalInfo?
    suspend fun findOverdueNotificationTargets(today: LocalDate): List<OverdueNotificationTarget>
}
