package plain.bookmaru.domain.inventory.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity

interface BookAffiliationRepository : JpaRepository<BookAffiliationEntity, Long>