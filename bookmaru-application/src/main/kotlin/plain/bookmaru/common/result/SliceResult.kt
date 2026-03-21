package plain.bookmaru.common.result

data class SliceResult<T>(
    val content: List<T>,
    val isLastPage: Boolean
)