package plain.bookmaru.domain.manager.port.out.result

data class RentalRequestCheckResult(
    val memberId: Long,
    val nickName: String,
    val title: String,
    val callNumber: String
)
