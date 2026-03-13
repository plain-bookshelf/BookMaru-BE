package plain.bookmaru.domain.book.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.book.persistent.entity.BookEntity

interface BookRepository : JpaRepository<BookEntity, Long> {
}