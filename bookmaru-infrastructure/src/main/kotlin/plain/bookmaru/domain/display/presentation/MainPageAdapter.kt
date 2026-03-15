package plain.bookmaru.domain.display.presentation

import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.port.`in`.AppViewMainPageUseCase
import plain.bookmaru.domain.display.port.`in`.WebViewMainPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageCommand
import plain.bookmaru.domain.display.vo.BookFindType

@RestController
@RequestMapping("/main")
class MainPageAdapter(
    private val appViewMainPageUseCase: AppViewMainPageUseCase,
    private val webViewMainPageUseCase: WebViewMainPageUseCase
) {

    @GetMapping()
    @LogExecution
    suspend fun appViewMainPage(
        @RequestParam(defaultValue = "20") pageable: Pageable,
        @RequestParam platformType: String,
        @RequestParam bookFindType: String
    ) : ResponseEntity<SuccessResponse>{
        val pageCommand = PageCommand(
            page = pageable.pageNumber,
            size = pageable.pageSize
        )

        val viewMainPageCommand = ViewMainPageCommand(
            bookFindType = BookFindType.valueOf(bookFindType),
            pageCommand = pageCommand
        )

        val actualPlatformType = PlatformType.valueOf(platformType)

        return when(actualPlatformType) {
            PlatformType.WEB -> responseMainPage(
                webViewMainPageUseCase.webExecute(pageCommand, actualPlatformType))

            PlatformType.IOS, PlatformType.ANDROID -> responseMainPage(
                appViewMainPageUseCase.appExecute(viewMainPageCommand, actualPlatformType))
        }
    }

    private suspend fun responseMainPage(content: Any): ResponseEntity<SuccessResponse> = ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "메인 페이지 정보를 가져오는데 성공했습니다.", content))
}