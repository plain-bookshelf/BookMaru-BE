package plain.bookmaru.domain.manager.port.`in`.command

import plain.bookmaru.domain.auth.vo.PlatformType

data class RentalRequestCheckCommand(
    val affiliationId: Long,
    val platformType: PlatformType
)
