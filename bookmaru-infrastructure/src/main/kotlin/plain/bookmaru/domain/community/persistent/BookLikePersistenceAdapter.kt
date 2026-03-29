package plain.bookmaru.domain.community.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.community.model.BookLike
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId
import plain.bookmaru.domain.community.persistent.mapper.BookLikeMapper
import plain.bookmaru.domain.community.persistent.repository.BookLikeRepository
import plain.bookmaru.domain.community.port.out.BookLikePort
import plain.bookmaru.global.config.DbProtection

@Component
class BookLikePersistenceAdapter(
    private val dbProtection: DbProtection,
    private val bookLikeMapper: BookLikeMapper,
    private val bookLikeRepository: BookLikeRepository
) : BookLikePort {

    override suspend fun findByBookAffiliationIdAndMemberId(
        bookAffiliationId: Long,
        memberId: Long
    ): BookLike? = dbProtection.withReadOnly {
        val embeddedId = BookLikeEmbeddedId(bookAffiliationId, memberId)
        val bookLikeEntity = bookLikeRepository.findBookLikeEntityById(embeddedId)

        bookLikeEntity?.let { bookLikeMapper.toDomain(it) }
    }

    override suspend fun save(bookLike: BookLike) {
        bookLikeRepository.save(bookLikeMapper.toEntity(bookLike))
    }

    override suspend fun delete(bookLike: BookLike) {
        val bookLikeEntity = bookLikeMapper.toEntity(bookLike)
        bookLikeRepository.delete(bookLikeEntity)
    }
}