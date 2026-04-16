package plain.bookmaru.domain.event.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.event.port.`in`.ViewEventDetailPageUseCase
import plain.bookmaru.domain.event.port.`in`.command.ViewEventDetailPageCommand

@RestController
@RequestMapping("/event/{eventId}")
class EventDetailAdapter(
    private val viewEventDetailPageUseCase: ViewEventDetailPageUseCase
) {

    @GetMapping
    @LogExecution
    suspend fun viewEventDetailPage(
        @PathVariable eventId: Long,
    ): ResponseEntity<SuccessResponse> {
        val command = ViewEventDetailPageCommand(eventId)

        val result = viewEventDetailPageUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "이벤트 상세 정보를 가져오는데 성공했습니다.", result))
    }
}