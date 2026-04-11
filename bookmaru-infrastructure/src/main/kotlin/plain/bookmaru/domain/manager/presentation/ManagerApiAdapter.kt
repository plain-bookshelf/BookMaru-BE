package plain.bookmaru.domain.manager.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.lending.port.`in`.ReturnUseCase
import plain.bookmaru.domain.lending.port.`in`.command.ReturnCommand

@RestController
@RequestMapping("/api/manager")
class ManagerApiAdapter(
    private val returnUseCase: ReturnUseCase
) {

    @PatchMapping("/returnBook/{bookDetailId}")
    @LogExecution
    suspend fun returnBook(
        @PathVariable bookDetailId: Long
    ) : ResponseEntity<SuccessResponse> {
        val command = ReturnCommand(
            bookDetailId = bookDetailId
        )

        returnUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "책 반납 완료", ""))
    }
}