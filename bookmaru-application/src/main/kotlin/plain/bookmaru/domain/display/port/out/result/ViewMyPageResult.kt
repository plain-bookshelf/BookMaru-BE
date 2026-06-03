package plain.bookmaru.domain.display.port.out.result

data class ViewMyPageResult(
    val profileImage: String,
    val nickname: String,
    val mostLittleLeftRentalTitle: String,
    val mostLittleLeftRentalDate: Int,
    val rentedBookCount: Int,
    val reservedBookCount: Int,
    val overdueBookCount: Int
)
