package plain.bookmaru.domain.community.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.community.exception.NotFoundCommentException
import plain.bookmaru.domain.community.model.Comment
import plain.bookmaru.domain.community.persistent.entity.QBookCommentEntity
import plain.bookmaru.domain.community.persistent.entity.QBookCommentLikeEntity
import plain.bookmaru.domain.community.persistent.mapper.BookCommentMapper
import plain.bookmaru.domain.community.persistent.repository.BookCommentRepository
import plain.bookmaru.domain.community.port.out.CommentPort
import plain.bookmaru.domain.display.port.out.result.CommentResult
import plain.bookmaru.domain.inventory.persistent.repository.BookAffiliationRepository
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection

@Component
class CommentPersistenceAdapter(
    private val queryFactory: JPAQueryFactory,
    private val bookCommentRepository: BookCommentRepository,
    private val memberRepository: MemberRepository,
    private val bookAffiliationRepository: BookAffiliationRepository,
    private val bookCommentMapper: BookCommentMapper,
    private val dbProtection: DbProtection
) : CommentPort {
    private val comment = QBookCommentEntity.bookCommentEntity
    private val commentLike = QBookCommentLikeEntity.bookCommentLikeEntity
    private val member = QMemberEntity.memberEntity

    override suspend fun findByBookAffiliationId(
        bookAffiliationId: Long,
        memberId: Long,
        pageCommend: PageCommand
    ): SliceResult<CommentResult> = dbProtection.withReadOnly {
        val size = pageCommend.size

        val limit = size.toLong()

        val results = queryFactory
            .select(
                Projections.constructor(
                    CommentResult::class.java,
                    member.profileImage,
                    comment.id,
                    member.nickname,
                    comment.comment,
                    comment.likeCount,
                    commentLike.id.memberId.isNotNull
                )
            )
            .from(comment)
            .leftJoin(comment.memberEntity, member)
            .leftJoin(commentLike).on(
                commentLike.id.bookCommentId.eq(comment.id),
                commentLike.id.memberId.eq(memberId)
            )
            .where(
                comment.bookAffiliationEntity.id.eq(bookAffiliationId)
            )
            .orderBy(comment.createdAt.desc())
            .offset(pageCommend.offset)
            .limit(limit)
            .fetch()

        return@withReadOnly sliceResult(results, size)
    }

    override suspend fun findById(commentId: Long): Comment = dbProtection.withReadOnly {
        val entity = bookCommentRepository.findByIdOrNull(commentId)
            ?: throw NotFoundCommentException("$commentId 아이디를 가진 책 정보를 찾지 못 했습니다.")

        return@withReadOnly bookCommentMapper.toDomain(entity)
    }

    override suspend fun save(comment: Comment, bookAffiliationId: Long?, memberId: Long?): Comment = dbProtection.withTransaction {
        if (comment.id == null) {
            val bookAffiliation = bookAffiliationRepository.getReferenceById(bookAffiliationId!!)
            val memberEntity = memberRepository.getReferenceById(memberId!!)

            val entity = bookCommentMapper.toEntity(comment, memberEntity, bookAffiliation)
            val savedEntity = bookCommentRepository.save(entity)

            return@withTransaction bookCommentMapper.toDomain(savedEntity)
        } else {
            val existingEntity = bookCommentRepository.findById(comment.id!!)
                .orElseThrow {  throw NotFoundCommentException("유저의 댓글 정보를 가져오지 못 했습니다.") }

            bookCommentMapper.updateEntity(comment, existingEntity)
            val savedEntity = bookCommentRepository.save(existingEntity)

            return@withTransaction bookCommentMapper.toDomain(savedEntity)
        }
    }

    override suspend fun delete(commentId: Long) = dbProtection.withTransaction {
        bookCommentRepository.deleteById(commentId)
    }

    override fun incrementLikeCount(commentId: Long) {
        queryFactory.update(comment)
            .set(comment.likeCount, comment.likeCount.add(1))
            .where(comment.id.eq(commentId))
            .execute()
    }

    override fun decrementLikeCount(commentId: Long) {
        queryFactory.update(comment)
            .set(comment.likeCount, comment.likeCount.add(-1))
            .where(comment.id.eq(commentId), comment.likeCount.gt(0))
            .execute()
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
