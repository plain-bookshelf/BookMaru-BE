package plain.bookmaru.domain.verification.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.verification.model.OfficialCode
import plain.bookmaru.domain.verification.port.`in`.OfficialCodeCreateUseCase
import plain.bookmaru.domain.verification.port.`in`.command.OfficialCodeCommand
import plain.bookmaru.domain.verification.port.out.OfficialCodePort

@Service
class OfficialCodeCreateService(
    private val officialCodePort: OfficialCodePort,
    private val affiliationPort: AffiliationPort
) : OfficialCodeCreateUseCase {

    override suspend fun execute(command: OfficialCodeCommand) {
        val affiliationName = command.affiliationName

        val affiliation = affiliationPort.findByAffiliationName(affiliationName)
            ?: throw NotFoundAffiliationException("$affiliationName 를 찾지 못 했습니다.")

        val officialCodeList = OfficialCode.create(affiliation.id!!)

        officialCodePort.save(officialCodeList, affiliation)
    }
}