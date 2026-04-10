package plain.bookmaru.common.command

data class PageCommand(
    val page: Int,
    val size: Int
) {
    val offset: Int
        get() = (page * size)
}
