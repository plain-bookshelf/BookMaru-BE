package plain.bookmaru.domain.lending.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.lending.persistent.entity.BookRentalRecordEntity

interface BookRentalRecordRepository : JpaRepository<BookRentalRecordEntity, Long> {
}