package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.port.`in`.command.AffiliationInfoChangeCommand

data class AffiliationInfoChangeRequestDto(
    val affiliationName: String
) {
    fun toCommand(platformType: String, username: String) : AffiliationInfoChangeCommand = AffiliationInfoChangeCommand(
        username,
        affiliationName,
        PlatformType.valueOf(platformType)
    )
}
