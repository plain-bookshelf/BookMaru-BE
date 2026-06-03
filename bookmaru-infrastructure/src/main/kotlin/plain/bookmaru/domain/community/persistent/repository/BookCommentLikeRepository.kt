package plain.bookmaru.domain.community.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.community.persistent.entity.BookCommentLikeEntity
import plain.bookmaru.domain.community.persistent.entity.embedded.BookCommentLikeEmbeddedId

interface BookCommentLikeRepository : JpaRepository<BookCommentLikeEntity, BookCommentLikeEmbeddedId> {
}