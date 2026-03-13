package plain.bookmaru.domain.community.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.community.persistent.entity.BookLikeEntity

interface BookLikeRepository : JpaRepository<BookLikeEntity, Long> {
}