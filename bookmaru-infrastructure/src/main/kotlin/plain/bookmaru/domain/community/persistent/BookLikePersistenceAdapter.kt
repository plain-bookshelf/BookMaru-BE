package plain.bookmaru.domain.community.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.community.model.BookLike
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId
import plain.bookmaru.domain.community.persistent.mapper.BookLikeMapper
import plain.bookmaru.domain.community.persistent.repository.BookLikeRepository
import plain.bookmaru.domain.community.port.out.BookLikePort
import plain.bookmaru.domain.inventory.persistent.repository.BookAffiliationRepository
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection

@Component
class BookLikePersistenceAdapter(
    private val dbProtection: DbProtection,
    private val bookLikeMapper: BookLikeMapper,
    private val bookLikeRepository: BookLikeRepository,
    private val memberRepository: MemberRepository,
    private val bookAffiliationRepository: BookAffiliationRepository
) : BookLikePort {

    override suspend fun findByBookAffiliationIdAndMemberId(
        bookAffiliationId: Long,
        memberId: Long
    ): BookLike? = dbProtection.withReadOnly {
        val embeddedId = BookLikeEmbeddedId(bookAffiliationId, memberId)
        val bookLikeEntity = bookLikeRepository.findBookLikeEntityById(embeddedId)

        bookLikeEntity?.let { bookLikeMapper.toDomain(it) }
    }

    override fun save(bookLike: BookLike) {
        val memberEntity = memberRepository.getReferenceById(bookLike.memberId)
        val bookAffiliationEntity = bookAffiliationRepository.getReferenceById(bookLike.bookAffiliationId)

        bookLikeRepository.save(bookLikeMapper.toEntity(bookLike, memberEntity, bookAffiliationEntity))
    }

    override fun delete(bookLike: BookLike) {
        val memberEntity = memberRepository.getReferenceById(bookLike.memberId)
        val bookAffiliationEntity = bookAffiliationRepository.getReferenceById(bookLike.bookAffiliationId)

        val bookLikeEntity = bookLikeMapper.toEntity(bookLike, memberEntity, bookAffiliationEntity)
        bookLikeRepository.delete(bookLikeEntity)
    }
}