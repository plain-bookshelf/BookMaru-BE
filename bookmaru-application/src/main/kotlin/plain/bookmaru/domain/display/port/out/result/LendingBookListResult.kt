package plain.bookmaru.domain.display.port.out.result

data class LendingBookListResult(
    val rentalBookInfo: List<RentalBookInfo> = emptyList(),
    val reservationBookInfo: List<ReservationBookInfo> = emptyList(),
    val overDueBookInfo: List<OverDueBookInfo> = emptyList()
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