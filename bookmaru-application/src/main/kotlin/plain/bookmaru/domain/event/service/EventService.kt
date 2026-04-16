package plain.bookmaru.domain.event.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.event.model.Event
import plain.bookmaru.domain.event.port.`in`.EventCreateUseCase
import plain.bookmaru.domain.event.port.`in`.command.EventCreateCommand
import plain.bookmaru.domain.event.port.out.EventPort
import plain.bookmaru.domain.event.vo.EventContent
import plain.bookmaru.domain.event.vo.EventInfo
import plain.bookmaru.domain.event.vo.EventType
import java.time.LocalDateTime

@Service
class EventService(
    private val eventPort: EventPort
) : EventCreateUseCase{
    override suspend fun execute(command: EventCreateCommand) {
        val memberId = command.memberId
        val startAt = command.startAt
        val endAt = command.endAt
        val now = LocalDateTime.now()

        val status = if (now < startAt) {
            EventType.NOT_STARTED
        } else if (endAt < now) {
            EventType.DONE
        } else {
            EventType.IN_PROGRESS
        }

        val event = Event(
            memberId = memberId,
            eventInfo = EventInfo(
                title = command.title,
                status = status,
                imageUrl = command.imageUrl,
                startAt = startAt,
                endAt = endAt,
            ),
            eventContent = EventContent(
                content = command.content
            )
        )

        eventPort.create(event)
    }
}