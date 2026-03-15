package plain.bookmaru.domain.book.port.out

import plain.bookmaru.domain.book.model.Genre

interface BookGenrePort {
    suspend fun loadBookGenre(ids: List<Long>): Map<Long, List<Genre>>
}