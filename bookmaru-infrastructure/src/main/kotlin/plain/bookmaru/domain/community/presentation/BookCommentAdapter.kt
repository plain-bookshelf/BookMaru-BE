package plain.bookmaru.domain.community.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.community.port.`in`.BookCommentChangeUseCase
import plain.bookmaru.domain.community.port.`in`.BookCommentDeleteUseCase
import plain.bookmaru.domain.community.port.`in`.BookCommentWriteUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookCommentDeleteCommand
import plain.bookmaru.domain.community.presentation.dto.request.BookCommentRequestDto
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/comment")
class BookCommentAdapter(
    private val bookCommentWriteUseCase: BookCommentWriteUseCase,
    private val bookCommentChangeUseCase: BookCommentChangeUseCase,
    private val bookCommentDeleteUseCase: BookCommentDeleteUseCase
) {

    @PostMapping("/{bookAffiliationId}/write")
    @LogExecution
    suspend fun writeComment(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PathVariable bookAffiliationId: Long,
        @RequestBody request: BookCommentRequestDto
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toWriteCommand(
            memberId = principal.id,
            bookAffiliationId = bookAffiliationId
        )

        bookCommentWriteUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "댓글 작성을 성공하였습니다.", ""))
    }

    @PatchMapping("/{commentId}/change")
    @LogExecution
    suspend fun changeComment(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PathVariable commentId: Long,
        @RequestBody request: BookCommentRequestDto
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toChangeCommand(
            memberId = principal.id,
            commentId = commentId
        )

        bookCommentChangeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "댓글 정보가 성공적으로 수정 되었습니다.", ""))
    }

    @DeleteMapping("/{commentId}/delete")
    @LogExecution
    suspend fun deleteComment(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PathVariable commentId: Long
    ) : ResponseEntity<SuccessResponse> {
        val command = BookCommentDeleteCommand(principal.id, commentId)

        bookCommentDeleteUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "댓글 정보가 성공적으로 삭제 되었습니다.", ""))
    }
}