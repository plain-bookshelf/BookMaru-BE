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
import plain.bookmaru.domain.community.port.`in`.CommentLikeUseCase
import plain.bookmaru.domain.community.port.`in`.CommentUnLikeUseCase
import plain.bookmaru.domain.community.port.`in`.command.CommentLikeCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/comment/{commentId}")
class BookCommentLikeAdapter(
    private val commentLikeUseCase: CommentLikeUseCase,
    private val commentUnLikeUseCase: CommentUnLikeUseCase
) {
    @PostMapping("/like")
    @LogExecution
    suspend fun commentLike(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<SuccessResponse> {
        val command = CommentLikeCommand(principal.id, commentId)

        commentLikeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(SuccessResponse.success(CustomHttpStatus.NO_CONTENT, "댓글에 좋아요를 누르는데 성공했습니다.", ""))
    }

    @DeleteMapping("/unlike")
    @LogExecution
    suspend fun unlike(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<SuccessResponse> {
        val command = CommentLikeCommand(principal.id, commentId)

        commentUnLikeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(SuccessResponse.success(CustomHttpStatus.NO_CONTENT, "댓글 좋아요 취소에 성공 했습니다.", ""))
    }
}