package plain.bookmaru.domain.inventory.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.inventory.persistent.mapper.BookDetailMapper
import plain.bookmaru.domain.inventory.persistent.repository.BookDetailRepository
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.global.config.DbProtection

@Component
class BookDetailPersistenceAdapter(
    private val bookDetailRepository: BookDetailRepository,
    private val bookDetailMapper: BookDetailMapper,
    private val dbProtection: DbProtection
) : BookDetailPort {

}