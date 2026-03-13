package plain.bookmaru.domain.community.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.community.persistent.entity.BookCommentEntity

interface BookCommentRepository : JpaRepository<BookCommentEntity, Long> {
}