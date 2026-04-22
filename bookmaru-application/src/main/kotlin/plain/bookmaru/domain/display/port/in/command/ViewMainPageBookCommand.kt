package plain.bookmaru.domain.display.port.`in`.command

import plain.bookmaru.domain.auth.vo.PlatformType

data class ViewMainPageBookCommand(
    val affiliationId: Long,
    val platformType: PlatformType
)
