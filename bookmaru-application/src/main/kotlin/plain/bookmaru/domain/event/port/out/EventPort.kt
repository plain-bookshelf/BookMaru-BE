package plain.bookmaru.domain.event.port.out

import plain.bookmaru.domain.event.model.Event

interface EventPort {
    suspend fun findAll(): List<Event>?
    suspend fun findById(eventId: Long): Event?

    suspend fun deleteById(eventId: Long)

    suspend fun save(event: Event): Event
}
