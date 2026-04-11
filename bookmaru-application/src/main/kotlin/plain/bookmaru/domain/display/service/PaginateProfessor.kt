package plain.bookmaru.domain.display.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import kotlin.math.ceil

@Service
class PaginateProfessor {

    fun <T> paginate(allContents: List<T>, command: PageCommand): SliceResult<T>? {
        if (command.size <= 0) return SliceResult(content = emptyList(), isLastPage = true)

        val start = command.offset.toInt()
        val end = (start + command.size).coerceAtMost(allContents.size)
        val content = if (start >= allContents.size) emptyList() else allContents.subList(start, end)

        val isLastPage = (command.page + 1) >= ceil(allContents.size.toDouble() / command.size).toInt()

        return SliceResult(
            content = content,
            isLastPage = isLastPage,
        )
    }
}