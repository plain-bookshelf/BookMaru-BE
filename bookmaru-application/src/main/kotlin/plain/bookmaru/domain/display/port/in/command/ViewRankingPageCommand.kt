package plain.bookmaru.domain.display.port.`in`.command

import plain.bookmaru.common.command.PageCommand

data class ViewRankingPageCommand(
    val pageCommand: PageCommand,
    val affiliationId: Long
)