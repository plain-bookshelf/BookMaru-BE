package plain.bookmaru.domain.display.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.port.`in`.ViewMainPageEventUseCase
import plain.bookmaru.domain.display.port.`in`.ViewMainPagePopularBookUseCase
import plain.bookmaru.domain.display.port.`in`.ViewMainPageRecentBookUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageBookCommand
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageEventCommand
import plain.bookmaru.domain.display.presentation.dto.response.ViewMainPageAppBookResponseDto
import plain.bookmaru.domain.display.presentation.dto.response.ViewMainPageWebBookResponseDto
import plain.bookmaru.domain.display.vo.BookFindType
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/main")
class MainPageAdapter(
    private val viewMainPageEventUseCase: ViewMainPageEventUseCase,
    private val viewMainPagePopularBookUseCase: ViewMainPagePopularBookUseCase,
    private val viewMainPageRecentBookUseCase: ViewMainPageRecentBookUseCase
) {

    @GetMapping("/event")
    @LogExecution
    suspend fun viewMainPageEvent(
        @AuthenticationPrincipal principal: CustomUserDetails,
    ) : ResponseEntity<SuccessResponse>{
        val command = ViewMainPageEventCommand(
            principal.affiliationId
        )

        val result = viewMainPageEventUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "메인 페이지의 이벤트 정보를 가져오는데 성공했습니다.", result))
    }

    @GetMapping("/book")
    @LogExecution
    suspend fun viewMainPagePopularBook(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestParam bookFindType: String,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse>{
        val bookFindType = BookFindType.valueOf(bookFindType)

        val command = ViewMainPageBookCommand(
            affiliationId = principal.affiliationId,
            platformType = PlatformType.valueOf(platformType)
        )

        val enumPlatformType = PlatformType.valueOf(platformType)

        val result = when (bookFindType) {
            BookFindType.POPULAR -> viewMainPagePopularBookUseCase.popularBookExecute(command)
            BookFindType.RECENT -> viewMainPageRecentBookUseCase.recentBookExecute(command)
        }

        val response = if (enumPlatformType == PlatformType.WEB) {
            ViewMainPageWebBookResponseDto.from(result)
        } else {
            ViewMainPageAppBookResponseDto.from(result)
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "인기 책 조회에 성공했습니다.", response))
    }
}