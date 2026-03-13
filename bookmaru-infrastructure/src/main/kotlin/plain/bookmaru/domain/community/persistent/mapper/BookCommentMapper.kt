package plain.bookmaru.domain.community.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.community.model.Comment
import plain.bookmaru.domain.community.persistent.entity.BookCommentEntity
import plain.bookmaru.domain.community.vo.BookReact
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Component
class BookCommentMapper {

    fun toDomain(entity: BookCommentEntity) : Comment {
        return Comment(
            id = entity.id,
            memberId = entity.memberEntity.id!!,
            bookId = entity.bookEntity.id!!,
            bookReact = BookReact(
                comment = entity.comment,
                starCount = entity.starCount
            ),
            likeCount = entity.likeCount,
        )
    }

    fun toEntity(domain: Comment, memberEntity: MemberEntity, bookEntity: BookEntity) : BookCommentEntity {
        return BookCommentEntity(
            memberEntity = memberEntity,
            bookEntity = bookEntity
        ).apply {
            this.comment = domain.bookReact.comment
            this.starCount = domain.bookReact.starCount
            this.likeCount = domain.likeCount
        }
    }
}