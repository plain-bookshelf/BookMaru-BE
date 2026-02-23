package plain.bookmaru.domain.affiliation.persistent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.mapper.AffiliationMapper
import plain.bookmaru.domain.affiliation.persistent.repository.AffiliationRepository
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.global.config.DbProtection

@Component
class AffiliationPersistenceAdapter(
    private val affiliationRepository: AffiliationRepository,
    private val affiliationMapper: AffiliationMapper,
    private val dbProtection: DbProtection

) : AffiliationPort {
    override suspend fun findByAffiliationName(name: String): Affiliation? = dbProtection.withReadOnly {
        val entity = affiliationRepository.findByAffiliationName(name)

        entity?.let { affiliationMapper.toDomain(it) }
    }

    override suspend fun findAll(): List<Affiliation> = dbProtection.withReadOnly {
        affiliationRepository.findAll().map { affiliationMapper.toDomain(it) }
    }

    override suspend fun findById(id: Long): Affiliation? = dbProtection.withReadOnly {
        val entity = affiliationRepository.findByIdOrNull(id)

        entity?.let { affiliationMapper.toDomain(it) }
    }

}