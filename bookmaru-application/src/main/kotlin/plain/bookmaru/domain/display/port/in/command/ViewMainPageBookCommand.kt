package plain.bookmaru.domain.display.port.`in`.command

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.domain.auth.vo.PlatformType

data class ViewMainPageBookCommand(
    val pageCommand: PageCommand,
    val affiliationId: Long,
    val platformType: PlatformType
)
