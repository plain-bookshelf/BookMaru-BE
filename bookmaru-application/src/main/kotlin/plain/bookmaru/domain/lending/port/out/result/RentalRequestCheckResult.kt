package plain.bookmaru.domain.lending.port.out.result

import java.time.LocalDateTime

sealed interface RentalRequestCheckResult

data class AppRentalRequestCheckResult(
    val bookDetailId: Long,
    val memberId: Long,
    val nickName: String,
    val title: String,
    val callNumber: String
) : RentalRequestCheckResult

data class WebRentalRequestCheckResult(
    val bookDetailId: Long,
    val memberId: Long,
    val nickName: String,
    val title: String,
    val callNumber: String,
    val rentalRequestDate: LocalDateTime
) : RentalRequestCheckResult
