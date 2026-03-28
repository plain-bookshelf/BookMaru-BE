package plain.bookmaru.domain.community.persistent

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.domain.community.model.CommentLike
import plain.bookmaru.domain.community.persistent.entity.embedded.BookCommentLikeEmbeddedId
import plain.bookmaru.domain.community.persistent.mapper.BookCommentLikeMapper
import plain.bookmaru.domain.community.persistent.repository.BookCommentLikeRepository
import plain.bookmaru.domain.community.port.out.CommentLikePort
import plain.bookmaru.global.config.DbProtection

@Component
class CommentLikePersistenceAdapter(
    private val dbProtection: DbProtection,
    private val bookCommentLikeMapper: BookCommentLikeMapper,
    private val bookCommentLikeRepository: BookCommentLikeRepository
) : CommentLikePort {
    override suspend fun findByCommentIdAndMemberId(
        commentId: Long,
        memberId: Long
    ): CommentLike? = dbProtection.withReadOnly {
        val embeddedId = BookCommentLikeEmbeddedId(commentId, memberId)

        val entity = bookCommentLikeRepository.findByIdOrNull(embeddedId)

        if (entity != null) return@withReadOnly bookCommentLikeMapper.toDomain(entity)

        return@withReadOnly null
    }

    override suspend fun save(commentLike: CommentLike): Unit = dbProtection.withTransaction {
        val entity = bookCommentLikeMapper.toEntity(commentLike)

        bookCommentLikeRepository.save(entity)
    }

    override suspend fun delete(commentLike: CommentLike) {
        val entity = bookCommentLikeMapper.toEntity(commentLike)

        bookCommentLikeRepository.delete(entity)
    }
}