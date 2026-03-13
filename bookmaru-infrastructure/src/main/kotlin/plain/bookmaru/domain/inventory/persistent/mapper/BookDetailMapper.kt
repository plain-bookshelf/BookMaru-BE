package plain.bookmaru.domain.inventory.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.inventory.persistent.entity.BookDetailEntity
import plain.bookmaru.domain.inventory.vo.BookDetailDiscernment
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Component
class BookDetailMapper {
    
    fun toDomain(entity: BookDetailEntity) : BookDetail {
        return BookDetail(
            id = entity.id,
            bookId = entity.bookEntity.id!!,
            affiliationId = entity.affiliationEntity.id!!,
            bookDetailDiscernment = BookDetailDiscernment(
                registrationNumber = entity.registrationNumber,
                callNumber = entity.callNumber,
            )
        )
    }

    fun toEntity(
        domain: BookDetail,
        memberEntity: MemberEntity,
        bookEntity: BookEntity,
        affiliationEntity: AffiliationEntity
    ) : BookDetailEntity {
        return BookDetailEntity(
            memberEntity = memberEntity,
            bookEntity = bookEntity,
            affiliationEntity = affiliationEntity,
            registrationNumber = domain.bookDetailDiscernment.registrationNumber,
            callNumber = domain.bookDetailDiscernment.callNumber
        )
    }
}