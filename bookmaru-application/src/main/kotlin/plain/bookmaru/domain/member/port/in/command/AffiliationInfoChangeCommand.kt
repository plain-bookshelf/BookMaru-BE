package plain.bookmaru.domain.member.port.`in`.command

import plain.bookmaru.domain.auth.vo.PlatformType

data class AffiliationInfoChangeCommand(
    val username: String,
    val newAffiliationName: String,
    val platformType: PlatformType
)
