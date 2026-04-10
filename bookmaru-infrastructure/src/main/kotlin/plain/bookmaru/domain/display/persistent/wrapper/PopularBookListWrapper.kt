package plain.bookmaru.domain.display.persistent.wrapper

import kotlinx.serialization.Serializable
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult

@Serializable
data class PopularBookListWrapper(val books: List<PopularBookSortResult>)