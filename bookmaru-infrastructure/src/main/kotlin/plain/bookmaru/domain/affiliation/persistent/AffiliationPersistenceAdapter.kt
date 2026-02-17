package plain.bookmaru.domain.affiliation.persistent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.persistent.mapper.AffiliationMapper
import plain.bookmaru.domain.affiliation.persistent.repository.AffiliationRepository
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.affiliation.vo.Affiliation

@Component
class AffiliationPersistenceAdapter(
    private val affiliationRepository: AffiliationRepository,
    private val affiliationMapper: AffiliationMapper

) : AffiliationPort {
    override suspend fun findByAffiliationName(name: String): Affiliation = withContext(Dispatchers.IO) {
        val entity = affiliationRepository.findByAffiliationName(name)
            ?: throw NotFoundAffiliationException("$name not found")

        return@withContext affiliationMapper.toDomain(entity)
    }

    override suspend fun findAll(): List<Affiliation> = withContext(Dispatchers.IO) {
        affiliationRepository.findAll().map { affiliationMapper.toDomain(it) }
    }

}