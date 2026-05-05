package plain.bookmaru.domain.notification.presentation

import org.springframework.http.MediaType
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.notification.port.out.NotificationPort
import plain.bookmaru.domain.notification.presentation.dto.response.NotificationSnapshotResponse
import plain.bookmaru.global.security.userdetails.CustomUserDetails
import plain.bookmaru.global.sse.NotificationSseEmitterManager

@RestController
@RequestMapping("/api/notification")
class NotificationAdapter(
    private val notificationPort: NotificationPort,
    private val notificationSseEmitterManager: NotificationSseEmitterManager
) {

    @GetMapping
    suspend fun notifications(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<SuccessResponse> {
        val pageCommand = PageCommand(
            page = pageable.pageNumber,
            size = pageable.pageSize.coerceIn(1, 100)
        )

        val result = notificationPort.findByMemberId(principal.id, pageCommand)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "알림 조회를 성공했습니다.", result))
    }

    @GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun notification(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestHeader(name = "Last-Event-ID", required = false) lastEventId: String?
    ): SseEmitter {
        val emitter = notificationSseEmitterManager.subscribe(principal.id, lastEventId)
        val recentNotifications = notificationPort.findRecentByMemberId(principal.id, 20)

        notificationSseEmitterManager.sendToEmitter(
            emitter = emitter,
            eventName = "notification-snapshot",
            data = NotificationSnapshotResponse(recentNotifications)
        )

        return emitter
    }
}
