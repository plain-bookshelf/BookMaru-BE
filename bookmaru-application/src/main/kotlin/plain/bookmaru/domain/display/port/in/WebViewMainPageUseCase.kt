package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.port.out.result.ViewMainPageResult

interface WebViewMainPageUseCase {
    suspend fun webExecute(command: PageCommand, platformType: PlatformType) : ViewMainPageResult
}