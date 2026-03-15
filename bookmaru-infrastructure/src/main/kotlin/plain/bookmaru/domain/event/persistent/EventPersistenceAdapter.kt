package plain.bookmaru.domain.event.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.event.model.Event
import plain.bookmaru.domain.event.persistent.mapper.EventMapper
import plain.bookmaru.domain.event.persistent.repository.EventRepository
import plain.bookmaru.domain.event.port.out.EventPort

@Component
class EventPersistenceAdapter(
    private val eventRepository: EventRepository,
    private val eventMapper: EventMapper
) : EventPort{
    override suspend fun findAll(): List<Event> = eventMapper.toDomainList(eventRepository.findAll())
}