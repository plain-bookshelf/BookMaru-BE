package plain.bookmaru.domain.book.persistent.mapper

import org.springframework.stereotype.Component
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
            ),
            rentalCount = entity.rentalCount,
            reservationCount = entity.reservationCount,
            likeCount = entity.likeCount,
            similarityToken = entity.similarityToken,
        )
    }

    fun toEntity(domain: Book): BookEntity {
        return BookEntity(
            title = domain.bookInfo.title,
            author = domain.bookInfo.author,
            publicationDate = domain.bookInfo.publicationDate,
            bookImage = domain.bookInfo.bookImage,
            publisher = domain.bookInfo.publisher,
            introduction = domain.bookInfo.introduction,
            similarityToken = domain.similarityToken,
        )
    }
}