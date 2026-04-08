package plain.bookmaru.domain.display.port.out.result

data class LendingBookListResult(
    val rentalBookInfo: List<RentalBookInfo>?,
    val reservationBookInfo: List<ReservationBookInfo>?,
    val overDueBookInfo: List<OverDueBookInfo>?
) {
    data class RentalBookInfo(
        val bookAffiliationId: Long,
        val bookImage: String,
        val title: String,
        val leftRentalDate: Int
    )

    data class ReservationBookInfo(
        val bookAffiliationId: Long,
        val bookImage: String,
        val title: String,
        val rank: Int
    )

    data class OverDueBookInfo(
        val bookAffiliationId: Long,
        val bookImage: String,
        val title: String,
        val overdueDate: Int
    )
}