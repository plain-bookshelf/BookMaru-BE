package plain.bookmaru.domain.search.port.`in`.command

import plain.bookmaru.common.command.PageCommand

data class BookAffiliationSearchCommand(
    val pageCommand: PageCommand,
    val affiliationId: Long,
    val keyword: String
)
