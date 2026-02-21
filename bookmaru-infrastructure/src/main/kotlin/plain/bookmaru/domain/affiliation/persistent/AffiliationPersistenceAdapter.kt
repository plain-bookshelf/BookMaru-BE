package plain.bookmaru.domain.affiliation.persistent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.mapper.AffiliationMapper
import plain.bookmaru.domain.affiliation.persistent.repository.AffiliationRepository
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.affiliation.model.Affiliation

@Component
class AffiliationPersistenceAdapter(
    private val affiliationRepository: AffiliationRepository,
    private val affiliationMapper: AffiliationMapper

) : AffiliationPort {
    override suspend fun findByAffiliationName(name: String): Affiliation? = withContext(Dispatchers.IO) {
        val entity = affiliationRepository.findByAffiliationName(name)

        return@withContext affiliationMapper.toDomain(entity)
    }

    override suspend fun findAll(): List<Affiliation> = withContext(Dispatchers.IO) {
        affiliationRepository.findAll().map { affiliationMapper.toDomain(it) }
    }

    override suspend fun findById(id: Long): Affiliation? = withContext(Dispatchers.IO) {
        val entity = affiliationRepository.findByIdOrNull(id)

        return@withContext affiliationMapper.toDomain(entity)
    }

}