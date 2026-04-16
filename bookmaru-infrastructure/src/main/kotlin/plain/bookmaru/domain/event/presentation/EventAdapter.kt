package plain.bookmaru.domain.event.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.event.port.`in`.EventCreateUseCase
import plain.bookmaru.domain.event.presentation.dto.request.EventCreateRequestDto
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/event")
class EventAdapter(
    private val eventCreateUseCase: EventCreateUseCase,
) {

    @PostMapping("/create")
    @LogExecution
    suspend fun create(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody request: EventCreateRequestDto
    ): ResponseEntity<SuccessResponse> {
        val command = request.toCommand(principal)

        eventCreateUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "이벤트 생성을 성공했습니다.", ""))
    }
}