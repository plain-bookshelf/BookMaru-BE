package plain.bookmaru.domain.event.port.out

import plain.bookmaru.domain.event.port.out.result.ViewEventDetailPageResult

interface EventDetailPort {
    suspend fun findById(eventId: Long): ViewEventDetailPageResult?
}