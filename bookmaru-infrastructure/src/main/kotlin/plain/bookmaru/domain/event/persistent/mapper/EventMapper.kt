package plain.bookmaru.domain.event.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.event.model.Event
import plain.bookmaru.domain.event.persistent.entity.EventDetailEntity
import plain.bookmaru.domain.event.persistent.entity.EventEntity
import plain.bookmaru.domain.event.vo.EventContent
import plain.bookmaru.domain.event.vo.EventInfo
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Component
class EventMapper {

    fun toDomain(eventEntity: EventEntity, eventDetailEntity: EventDetailEntity) : Event {
        return Event(
            id = eventEntity.id,
            memberId = eventEntity.memberEntity.id!!,
            eventInfo = EventInfo(
                title = eventEntity.title,
                status = eventEntity.status,
                imageUrl = eventEntity.imageUrl,
                startAt = eventEntity.startAt,
                endAt = eventEntity.endAt,
            ),
            eventContent = EventContent(eventDetailEntity.content),
        )
    }

    fun toEntity(domain: Event, member: MemberEntity) : Pair<EventEntity, EventDetailEntity?> {
        val eventEntity = EventEntity(
            id = domain.id,
            title = domain.eventInfo.title,
            memberEntity = member,
            status = domain.eventInfo.status,
            imageUrl = domain.eventInfo.imageUrl,
            startAt = domain.eventInfo.startAt,
            endAt = domain.eventInfo.endAt,
        )

        val eventDetailEntity = EventDetailEntity(
            id = domain.id,
            content = domain.eventContent.content,
            event = eventEntity,
        )

        return Pair(eventEntity, eventDetailEntity)
    }
}