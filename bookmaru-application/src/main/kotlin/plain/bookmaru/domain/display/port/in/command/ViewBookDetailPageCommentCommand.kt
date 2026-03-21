package plain.bookmaru.domain.display.port.`in`.command

import plain.bookmaru.common.command.PageCommand

data class ViewBookDetailPageCommentCommand(
    val pageCommand: PageCommand,
    val bookAffiliationId: Long
)