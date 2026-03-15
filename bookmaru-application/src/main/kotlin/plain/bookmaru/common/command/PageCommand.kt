package plain.bookmaru.common.command

data class PageCommand(
    val page: Int,
    val size: Int
) {
    val offset: Long
        get() = (page * size).toLong()
}
