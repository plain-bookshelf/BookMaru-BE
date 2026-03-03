package plain.bookmaru.domain.affiliation.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.transaction.annotation.Transactional
import plain.bookmaru.common.annotation.ReadOnlyService
import plain.bookmaru.domain.affiliation.port.`in`.AffiliationViewUseCase
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.affiliation.port.out.result.AffiliationViewResult

private val log = KotlinLogging.logger {}

@ReadOnlyService
class AffiliationViewService(
    private val affiliationPort: AffiliationPort
) : AffiliationViewUseCase {

    @Transactional
    override suspend fun execute(): AffiliationViewResult
    = AffiliationViewResult(
        affiliationPort.findAll()
    ).also {
        log.info { "조회 완료" }
    }
}