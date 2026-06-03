package plain.bookmaru.domain.display.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.display.port.`in`.ViewRankingPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewRankingPageCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/ranking")
class RankingPageAdapter(
    private val viewRankingPageUseCase: ViewRankingPageUseCase
) {

    @GetMapping
    @LogExecution
    suspend fun getRankingPage(
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<SuccessResponse> {
        val command = ViewRankingPageCommand(
            affiliationId = principal.affiliationId
        )

        val result = viewRankingPageUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "ranking 데이터를 가져오는데 성공했습니다.", result))
    }
}