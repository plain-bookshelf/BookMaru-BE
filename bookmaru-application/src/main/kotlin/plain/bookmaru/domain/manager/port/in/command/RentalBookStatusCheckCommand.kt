package plain.bookmaru.domain.manager.port.`in`.command

import plain.bookmaru.common.command.PageCommand

data class RentalBookStatusCheckCommand(
    val pageCommand: PageCommand,
    val affiliationId: Long,
    val nickname: String? = ""
)