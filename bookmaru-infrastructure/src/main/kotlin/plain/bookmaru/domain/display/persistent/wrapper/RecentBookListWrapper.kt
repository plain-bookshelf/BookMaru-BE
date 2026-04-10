package plain.bookmaru.domain.display.persistent.wrapper

import kotlinx.serialization.Serializable
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult

@Serializable
data class RecentBookListWrapper(val books: List<RecentBookSortResult>)