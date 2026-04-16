package plain.bookmaru.domain.event.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.event.exception.NotFoundEventException
import plain.bookmaru.domain.event.port.`in`.ViewEventDetailPageUseCase
import plain.bookmaru.domain.event.port.`in`.command.ViewEventDetailPageCommand
import plain.bookmaru.domain.event.port.out.EventDetailPort
import plain.bookmaru.domain.event.port.out.result.ViewEventDetailPageResult

@Service
class EventDetailService(
    private val eventDetailPort: EventDetailPort
) : ViewEventDetailPageUseCase{
    override suspend fun execute(command: ViewEventDetailPageCommand): ViewEventDetailPageResult {
        val eventId = command.eventId
        val result = eventDetailPort.findById(eventId)
            ?: throw NotFoundEventException("eventId: $eventId 이벤트 정보를 찾지 못 했습니다.")

        return result
    }
}