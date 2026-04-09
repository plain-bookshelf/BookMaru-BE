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
import plain.bookmaru.domain.display.port.`in`.ViewMyPageLendingInfoUseCase
import plain.bookmaru.domain.display.port.`in`.ViewMyPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageCommand
import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageLendingInfoCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/myPage")
class MyPageAdapter(
    private val viewMyPageUseCase: ViewMyPageUseCase,
    private val viewMyPageLendingInfoUseCase: ViewMyPageLendingInfoUseCase
) {

    @GetMapping()
    @LogExecution
    suspend fun getMyPage(
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = ViewMyPageCommand(
            username = principal.username.toString()
        )

        val result = viewMyPageUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "마이페이지 조회에 성공했습니다.", result))
    }

    @GetMapping("/lendingInfo")
    @LogExecution
    suspend fun getLendingInfo(
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<SuccessResponse> {
        val command = ViewMyPageLendingInfoCommand(
            memberId = principal.id
        )

        val result = viewMyPageLendingInfoUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "마이페이지의 유저 책 관련 정보들을 가져오는데 성공했습니다.", result))
    }
}