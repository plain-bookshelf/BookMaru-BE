package plain.bookmaru.domain.verification.persistent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
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
    private val affiliationMapper: AffiliationMapper,
    @Qualifier("virtualDispatcher") private val virtualDispatcher: CoroutineDispatcher
) : OfficialCodePort {
    override suspend fun save(officialCodes: List<OfficialCode>, affiliation: Affiliation) : List<OfficialCode> = withContext(virtualDispatcher) {
        officialCodeRepository.saveAll(officialCodeMapper.toEntityList(officialCodes, affiliation))
            .map { officialCodeMapper.toDomain(it) }
    }

    override suspend fun load(
        code: String,
        affiliation: Affiliation
    ) : OfficialCode? = withContext(virtualDispatcher) {
        officialCodeRepository.findByAffiliationAndCode(affiliationMapper.toEntity(affiliation), code)?.let {
            officialCodeMapper.toDomain(it)
        }
    }
}