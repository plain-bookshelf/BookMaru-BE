package plain.bookmaru.domain.community.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.community.persistent.entity.BookCommentLikeEntity

interface BookCommentLikeRepository : JpaRepository<BookCommentLikeEntity, Long> {
}