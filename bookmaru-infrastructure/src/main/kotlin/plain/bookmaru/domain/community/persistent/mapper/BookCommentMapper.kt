package plain.bookmaru.domain.community.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.community.model.Comment
import plain.bookmaru.domain.community.persistent.entity.BookCommentEntity
import plain.bookmaru.domain.community.vo.BookReact
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Component
class BookCommentMapper {

    fun toDomain(entity: BookCommentEntity) : Comment {
        return Comment(
            id = entity.id,
            memberId = entity.memberEntity.id!!,
            bookAffiliationId = entity.bookAffiliationEntity.id!!,
            bookReact = BookReact(
                comment = entity.comment,
                starCount = entity.starCount
            ),
            likeCount = entity.likeCount,
        )
    }

    fun toEntity(domain: Comment, memberEntity: MemberEntity, bookAffiliationEntity: BookAffiliationEntity) : BookCommentEntity {
        return BookCommentEntity(
            memberEntity = memberEntity,
            bookAffiliationEntity = bookAffiliationEntity
        ).apply {
            this.comment = domain.bookReact.comment
            this.starCount = domain.bookReact.starCount
            this.likeCount = domain.likeCount
        }
    }

    fun toDomainList(entities: List<BookCommentEntity>) : List<Comment> {
        return entities.map { toDomain(it) }
    }

    fun updateEntity(domain: Comment, entity: BookCommentEntity) {
        entity.comment = domain.bookReact.comment
        entity.starCount = domain.bookReact.starCount
        entity.likeCount = domain.likeCount
    }
}