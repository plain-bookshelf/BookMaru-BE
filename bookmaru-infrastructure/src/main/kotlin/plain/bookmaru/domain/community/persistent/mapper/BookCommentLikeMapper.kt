package plain.bookmaru.domain.community.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.community.model.CommentLike
import plain.bookmaru.domain.community.persistent.entity.BookCommentLikeEntity
import plain.bookmaru.domain.community.persistent.entity.embedded.BookCommentLikeEmbeddedId

@Component
class BookCommentLikeMapper {

    fun toDomain(entity: BookCommentLikeEntity) : CommentLike {
        return CommentLike(
            memberId = entity.id.memberId,
            commentId = entity.id.bookCommentId
        )
    }

    fun toEntity(
        domain: CommentLike
    ) : BookCommentLikeEntity {
        val embeddedId = BookCommentLikeEmbeddedId(
            memberId = domain.memberId,
            bookCommentId = domain.commentId
        )

        return BookCommentLikeEntity(
            id = embeddedId,
        )
    }
}