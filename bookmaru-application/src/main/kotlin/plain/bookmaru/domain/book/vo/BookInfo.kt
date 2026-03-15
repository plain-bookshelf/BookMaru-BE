package plain.bookmaru.domain.book.vo

import java.time.LocalDate

data class BookInfo(
    val title: String,
    val author: String,
    val publicationDate: String,
    val introduction: String,
    val bookImage: String,
    val publisher: String,
    val registrationDate: LocalDate
)
