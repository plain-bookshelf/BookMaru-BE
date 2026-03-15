package plain.bookmaru.domain.display.port.out.result

import plain.bookmaru.common.result.PageResult

data class ViewMainPageResult(
    val eventInfoResultList: List<EventInfoResult>? = null,
    val popularBookSortResultList: PageResult<PopularBookSortResult>? = null,
    val recentBookSortResultList: PageResult<RecentBookSortResult>? = null
)