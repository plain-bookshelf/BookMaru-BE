package plain.bookmaru.domain.lending.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.persistent.mapper.RentalMapper
import plain.bookmaru.domain.lending.persistent.repository.BookRentalRecordRepository
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.global.config.DbProtection

@Component
class BookRentalRecordPersistenceAdapter(
    private val bookRentalRecordRepository: BookRentalRecordRepository,
    private val rentalMapper: RentalMapper,
    private val dbProtection: DbProtection
) : BookRentalRecordPort {

    override suspend fun save(renter: Rental): Unit = dbProtection.withTransaction  {
        val rentalEntity = rentalMapper.toEntity(renter)
        bookRentalRecordRepository.save(rentalEntity)
    }
}
