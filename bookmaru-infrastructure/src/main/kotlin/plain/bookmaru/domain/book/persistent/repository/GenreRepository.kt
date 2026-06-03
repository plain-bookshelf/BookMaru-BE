package plain.bookmaru.domain.book.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.book.persistent.entity.GenreEntity

interface GenreRepository : JpaRepository<GenreEntity, Long> {
}