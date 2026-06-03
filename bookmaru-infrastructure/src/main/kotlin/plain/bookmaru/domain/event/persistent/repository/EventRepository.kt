package plain.bookmaru.domain.event.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.event.persistent.entity.EventEntity

interface EventRepository : JpaRepository<EventEntity, Long> {
}