package plain.bookmaru.domain.event.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.event.port.`in`.EventChangeUseCase
import plain.bookmaru.domain.event.port.`in`.EventCreateUseCase
import plain.bookmaru.domain.event.presentation.dto.request.EventWriteRequestDto
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/event")
class EventAdapter(
    private val eventCreateUseCase: EventCreateUseCase,
    private val eventChangeUseCase: EventChangeUseCase,
) {

    @PostMapping("/create")
    @LogExecution
    suspend fun create(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody request: EventWriteRequestDto
    ): ResponseEntity<SuccessResponse> {
        val command = request.toCreateCommand(principal)

        eventCreateUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "이벤트 생성을 성공했습니다.", ""))
    }

    @PatchMapping("/{eventId}/change")
    @LogExecution
    suspend fun change(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PathVariable eventId: Long,
        @RequestBody request: EventWriteRequestDto
    ): ResponseEntity<SuccessResponse> {
        val command = request.toChangeCommand(principal, eventId)

        eventChangeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "이벤트 수정을 성공했습니다.", ""))
    }
}