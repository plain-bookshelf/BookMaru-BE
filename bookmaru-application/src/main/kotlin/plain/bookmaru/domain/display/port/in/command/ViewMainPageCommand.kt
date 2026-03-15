package plain.bookmaru.domain.display.port.`in`.command

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.domain.display.vo.BookFindType

data class ViewMainPageCommand(
    val bookFindType: BookFindType,
    val pageCommand: PageCommand
)