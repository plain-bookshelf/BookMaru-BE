package plain.bookmaru.domain.event.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.event.exception.NotEventCreateUserException
import plain.bookmaru.domain.event.exception.NotFoundEventException
import plain.bookmaru.domain.event.model.Event
import plain.bookmaru.domain.event.port.`in`.EventChangeUseCase
import plain.bookmaru.domain.event.port.`in`.EventCreateUseCase
import plain.bookmaru.domain.event.port.`in`.EventDeleteUseCase
import plain.bookmaru.domain.event.port.`in`.command.EventChangeCommand
import plain.bookmaru.domain.event.port.`in`.command.EventCreateCommand
import plain.bookmaru.domain.event.port.`in`.command.EventDeleteCommand
import plain.bookmaru.domain.event.port.out.EventPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.port.`in`.PublishNotificationUseCase
import plain.bookmaru.domain.notification.vo.NotificationInfo
import plain.bookmaru.domain.notification.vo.NotificationPayload
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.domain.notification.vo.TargetInfo
import plain.bookmaru.domain.notification.vo.TargetType
import plain.bookmaru.domain.event.vo.EventContent
import plain.bookmaru.domain.event.vo.EventInfo
import plain.bookmaru.domain.event.vo.EventType
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class EventService(
    private val eventPort: EventPort,
    private val memberPort: MemberPort,
    private val publishNotificationUseCase: PublishNotificationUseCase
) : EventCreateUseCase, EventChangeUseCase, EventDeleteUseCase {
    override suspend fun execute(command: EventCreateCommand) {
        val memberId = command.memberId
        val startAt = command.startAt
        val endAt = command.endAt
        val now = LocalDateTime.now()
        val creator = memberPort.findById(memberId)
            ?: throw NotFoundMemberException("memberId: $memberId 사용자를 찾을 수 없습니다.")

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

        val savedEvent = eventPort.save(event)
        val savedEventId = savedEvent.id ?: return
        val affiliationId = creator.affiliationId ?: return
        val notifications = memberPort.findAllByAffiliationId(affiliationId)
            .filter { it.id != memberId }
            .map { member ->
                Notification(
                    memberId = member.id!!,
                    targetInfo = TargetInfo(
                        targetId = savedEventId,
                        targetType = TargetType.EVENT
                    ),
                    notificationInfo = NotificationInfo(
                        name = "새로운 이벤트가 등록되었습니다.",
                        payload = NotificationPayload.EventPayload(
                            eventId = savedEventId,
                            title = savedEvent.eventInfo.title,
                            startDate = savedEvent.eventInfo.startAt.toString(),
                            endDate = savedEvent.eventInfo.endAt.toString()
                        ),
                        type = NotificationType.EVENT,
                        url = "/event/$savedEventId"
                    ),
                    isRead = false
                )
            }

        runCatching {
            publishNotificationUseCase.executeAll(notifications)
        }.onFailure {
            log.warn(it) { "이벤트 생성 알림 발행에 실패했습니다. eventId=$savedEventId" }
        }
    }

    override suspend fun execute(command: EventChangeCommand) {
        val event = eventPort.findById(command.eventId)
            ?: throw NotFoundEventException("eventId: ${command.eventId} 이벤트 정보를 찾지 못 했습니다.")

        val memberId = command.memberId
        val startAt = command.startAt
        val endAt = command.endAt
        val now = LocalDateTime.now()

        if (event.memberId != memberId)
            throw NotEventCreateUserException("memberId: $memberId 유저는 이 이벤트를 수정할 권한이 없습니다.")

        val status = if (now < startAt) {
            EventType.NOT_STARTED
        } else if (now > endAt) {
            EventType.DONE
        } else {
            EventType.IN_PROGRESS
        }

        event.updateEventInfo(
            EventInfo(
                title = command.title,
                status = status,
                imageUrl = command.imageUrl,
                startAt = startAt,
                endAt = endAt
            ),
            EventContent(command.content)
        )

        eventPort.save(event)
    }

    override suspend fun execute(command: EventDeleteCommand) {
        val eventId = command.eventId
        val memberId = command.memberId

        val event = eventPort.findById(eventId)
            ?: throw NotFoundEventException("eventId: $eventId 이벤트 정보를 찾지 못 했습니다.")

        if (event.memberId != memberId)
            throw NotEventCreateUserException("memberId: $memberId 유저는 이 이벤트를 수정할 권한이 없습니다.")

        eventPort.deleteById(eventId)
    }
}
