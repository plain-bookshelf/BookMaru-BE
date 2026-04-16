package plain.bookmaru.domain.event.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.event.exception.NotContainEventContentException
import plain.bookmaru.domain.event.model.Event
import plain.bookmaru.domain.event.persistent.mapper.EventMapper
import plain.bookmaru.domain.event.persistent.repository.EventDetailRepository
import plain.bookmaru.domain.event.persistent.repository.EventRepository
import plain.bookmaru.domain.event.port.out.EventPort
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection

@Component
class EventPersistenceAdapter(
    private val eventRepository: EventRepository,
    private val eventDetailRepository: EventDetailRepository,
    private val memberRepository: MemberRepository,
    private val eventMapper: EventMapper,
    private val dbProtection: DbProtection
) : EventPort{
    override suspend fun findAll(): List<Event>? = dbProtection.withReadOnly {
        eventMapper.toDomainList(eventRepository.findAll())
    }

    override suspend fun create(event: Event): Unit = dbProtection.withTransaction {
        val member = memberRepository.getReferenceById(event.memberId)
        val eventEntity = eventMapper.toEntity(event, member)
        val event = eventEntity.first

        val eventDetail = eventEntity.second
            ?: throw NotContainEventContentException()

        eventRepository.save(event)
        eventDetailRepository.save(eventDetail)
    }
}