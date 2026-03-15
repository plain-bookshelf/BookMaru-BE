package plain.bookmaru.domain.book.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.book.model.Book

interface BookPort {
    suspend fun loadPopularSort(command: PageCommand) : PageResult<Book>
    suspend fun loadRecentSort(command: PageCommand) : PageResult<Book>
}