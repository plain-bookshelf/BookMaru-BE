package plain.bookmaru.domain.inventory.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.BookDetailEntity
import plain.bookmaru.domain.inventory.vo.BookDetailDiscernment
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Component
class BookDetailMapper {
    
    fun toDomain(entity: BookDetailEntity) : BookDetail {
        return BookDetail(
            id = entity.id,
            bookAffiliationId = entity.bookAffiliationEntity.id!!,
            bookDetailDiscernment = BookDetailDiscernment(
                registrationNumber = entity.registrationNumber,
                callNumber = entity.callNumber,
            )
        )
    }

    fun toEntity(
        domain: BookDetail,
        memberEntity: MemberEntity,
        bookAffiliationEntity: BookAffiliationEntity,
    ) : BookDetailEntity {
        return BookDetailEntity(
            bookAffiliationEntity = bookAffiliationEntity,
            registrationNumber = domain.bookDetailDiscernment.registrationNumber,
            callNumber = domain.bookDetailDiscernment.callNumber
        ).apply {
            this.memberEntity = memberEntity
        }
    }

    fun toDomainList(entities: List<BookDetailEntity>) : List<BookDetail> {
        return entities.map { toDomain(it) }
    }
}