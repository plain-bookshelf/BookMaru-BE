package plain.bookmaru.domain.inventory.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.inventory.persistent.entity.BookDetailEntity

interface BookDetailRepository : JpaRepository<BookDetailEntity, Long> {
}