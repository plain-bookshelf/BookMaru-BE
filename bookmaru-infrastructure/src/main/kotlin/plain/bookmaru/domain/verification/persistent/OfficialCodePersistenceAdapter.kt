package plain.bookmaru.domain.verification.persistent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.domain.affiliation.persistent.mapper.AffiliationMapper
import plain.bookmaru.domain.verification.model.OfficialCode
import plain.bookmaru.domain.verification.persistent.mapper.OfficialCodeMapper
import plain.bookmaru.domain.verification.persistent.repository.OfficialCodeRepository
import plain.bookmaru.domain.verification.port.out.OfficialCodePort

@Component
class OfficialCodePersistenceAdapter(
    private val officialCodeRepository: OfficialCodeRepository,
    private val officialCodeMapper: OfficialCodeMapper,
    private val affiliationMapper: AffiliationMapper
) : OfficialCodePort {
    override suspend fun save(officialCodes: List<OfficialCode>, affiliation: Affiliation) {
        withContext(Dispatchers.IO) {
            officialCodeRepository.saveAll(officialCodeMapper.toEntityList(officialCodes, affiliation))
        }
    }

    override suspend fun load(
        code: String,
        affiliation: Affiliation
    ) : OfficialCode? = withContext(Dispatchers.IO) {
        officialCodeRepository.findByAffiliationAndCode(affiliationMapper.toEntity(affiliation), code)?.let {
            officialCodeMapper.toDomain(it)
        }
    }
}