package plain.bookmaru.domain.display.persistent.wrapper

import kotlinx.serialization.Serializable
import plain.bookmaru.domain.display.port.out.result.BookSortResult

@Serializable
data class BookListWrapper(val books: List<BookSortResult>)