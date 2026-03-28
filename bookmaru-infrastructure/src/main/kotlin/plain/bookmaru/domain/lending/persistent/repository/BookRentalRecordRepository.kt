package plain.bookmaru.domain.lending.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.lending.persistent.entity.BookRentalRecordEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookRentalRecordEmbeddedId

interface BookRentalRecordRepository : JpaRepository<BookRentalRecordEntity, BookRentalRecordEmbeddedId>