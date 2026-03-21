package plain.bookmaru.domain.community.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.community.persistent.entity.QBookCommentEntity
import plain.bookmaru.domain.community.port.out.CommentPort
import plain.bookmaru.domain.display.port.out.result.CommentResult
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
import plain.bookmaru.global.config.DbProtection

@Component
class CommentPersistenceAdapter(
    private val queryFactory: JPAQueryFactory,

    private val dbProtection: DbProtection
) : CommentPort {
    private val comment = QBookCommentEntity.bookCommentEntity
    private val member = QMemberEntity.memberEntity

    override suspend fun findByBookAffiliationId(bookAffiliationId: Long, pageCommend: PageCommand): SliceResult<CommentResult> = dbProtection.withReadOnly {
        val size = pageCommend.size

        val limit = size.toLong()

        val results = queryFactory
            .select(
                Projections.constructor(
                    CommentResult::class.java,
                    member.profileImage,
                    member.nickname,
                    comment.comment,
                    comment.likeCount,
                    comment.starCount
                )
            )
            .from(comment)
            .leftJoin(comment.memberEntity, member)
            .where(
                comment.bookAffiliationEntity.id.eq(bookAffiliationId)
            )
            .orderBy(comment.createdAt.desc())
            .offset(pageCommend.offset)
            .limit(limit)
            .fetch()

        return@withReadOnly sliceResult(results, size)
    }

    /*
    private helper method
     */

    private fun sliceResult(results: List<CommentResult>, requestSize: Int): SliceResult<CommentResult> {
        val hasNext = results.size >= requestSize

        return SliceResult(
            content = results,
            isLastPage = !hasNext
        )
    }
}