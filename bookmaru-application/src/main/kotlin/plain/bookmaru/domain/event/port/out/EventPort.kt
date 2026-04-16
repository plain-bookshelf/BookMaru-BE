package plain.bookmaru.domain.event.port.out

import plain.bookmaru.domain.event.model.Event

interface EventPort {
    suspend fun findAll(): List<Event>?
}