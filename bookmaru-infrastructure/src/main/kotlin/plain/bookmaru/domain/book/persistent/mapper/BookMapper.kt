package plain.bookmaru.domain.book.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity.affiliationEntity
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.book.vo.BookInfo

@Component
class BookMapper {

    fun toDomain(entity: BookEntity) : Book {
        return Book(
            id = entity.id,
            bookInfo = BookInfo(
                title = entity.title,
                author = entity.author,
                publicationDate = entity.publicationDate,
                introduction = entity.introduction,
                bookImage = entity.bookImage,
                publisher = entity.publisher,
                affiliationName = entity.affiliationEntity.affiliationName
            )
        )
    }

    fun toEntity(domain: Book, affiliationEntity: AffiliationEntity): BookEntity {
        return BookEntity(
            affiliationEntity = affiliationEntity,
            title = domain.bookInfo.title,
            author = domain.bookInfo.author,
            publicationDate = domain.bookInfo.publicationDate,
            bookImage = domain.bookInfo.bookImage,
            publisher = domain.bookInfo.publisher,
            introduction = domain.bookInfo.introduction
        )
    }

    fun toEntityList(domains: List<Book>, affiliationEntity: AffiliationEntity): List<BookEntity>
        = domains.map { toEntity(it, affiliationEntity) }

    fun toDomainList(entities: List<BookEntity>) : List<Book>
        = entities.map { toDomain(it) }
}