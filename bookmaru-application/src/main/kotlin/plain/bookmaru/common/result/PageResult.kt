package plain.bookmaru.common.result

data class PageResult<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val isLastPage: Boolean = false
)
