package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageCommand
import plain.bookmaru.domain.display.port.out.result.ViewMainPageResult

interface AppViewMainPageUseCase {
    suspend fun appExecute(command: ViewMainPageCommand, platformType: PlatformType) : ViewMainPageResult
}