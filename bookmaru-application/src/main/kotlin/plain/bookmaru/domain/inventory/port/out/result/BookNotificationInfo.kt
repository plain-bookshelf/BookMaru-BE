package plain.bookmaru.domain.inventory.port.out.result

data class BookNotificationInfo(
    val bookAffiliationId: Long,
    val title: String,
    val bookImage: String
)
