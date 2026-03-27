package plain.bookmaru.domain.community.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.community.port.`in`.BookLikeUseCase
import plain.bookmaru.domain.community.port.`in`.BookUnLikeUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookLikeCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/bookDetail/{bookAffiliationId}")
class BookLikeAdapter(
    private val bookLikeUseCase: BookLikeUseCase,
    private val bookUnLikeUseCase: BookUnLikeUseCase
) {

    @PostMapping("/like")
    @LogExecution
    suspend fun bookLike(
        @PathVariable bookAffiliationId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = BookLikeCommand(
            bookAffiliationId = bookAffiliationId,
            memberId = principal.id
        )

        bookLikeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(SuccessResponse.success(CustomHttpStatus.NO_CONTENT, "책에 좋아요를 누르는데 성공했습니다.", ""))
    }

    @DeleteMapping("/unlike")
    @LogExecution
    suspend fun bookUnlike(
        @PathVariable bookAffiliationId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = BookLikeCommand(
            bookAffiliationId = bookAffiliationId,
            memberId = principal.id
        )

        bookUnLikeUseCase.bookUnLike(command)

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(SuccessResponse.success(CustomHttpStatus.NO_CONTENT, "책 좋아요를 취소하는데 성공했습니다.", ""))
    }
}