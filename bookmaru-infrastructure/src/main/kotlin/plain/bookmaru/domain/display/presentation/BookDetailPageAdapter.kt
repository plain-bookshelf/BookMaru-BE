package plain.bookmaru.domain.display.presentation

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.display.port.`in`.ViewBookDetailPageCommentUseCase
import plain.bookmaru.domain.display.port.`in`.ViewBookDetailPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewBookDetailPageCommand
import plain.bookmaru.domain.display.port.`in`.command.ViewBookDetailPageCommentCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/book/{bookAffiliationId}")
class BookDetailPageAdapter(
    private val viewBookDetailPageUseCase: ViewBookDetailPageUseCase,
    private val viewBookDetailPageCommentUseCase: ViewBookDetailPageCommentUseCase
) {

    @GetMapping
    @LogExecution
    suspend fun bookDetailPage(
        @PathVariable("bookAffiliationId") bookAffiliationId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<SuccessResponse> {
        val command = ViewBookDetailPageCommand(
            bookAffiliationId = bookAffiliationId,
            affiliationId = principal.affiliationId,
            memberId = principal.id
        )
        val result = viewBookDetailPageUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "책 상세 정보를 가져오는데 성공했습니다.", result))
    }

    @GetMapping("/comment")
    @LogExecution
    suspend fun commentPage(
        @PathVariable("bookAffiliationId") bookAffiliationId: Long,
        @PageableDefault(size = 20) pageable: Pageable
    ) : ResponseEntity<SuccessResponse> {
        val command = ViewBookDetailPageCommentCommand(
            pageCommand = PageCommand(
                size = pageable.pageSize,
                page = pageable.pageNumber
            ),
            bookAffiliationId = bookAffiliationId
        )

        val result = viewBookDetailPageCommentUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "책 상세정보의 댓글을 가져오는데 성공했습니다.", result))
    }
}