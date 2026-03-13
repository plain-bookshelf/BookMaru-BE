package plain.bookmaru.domain.event.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository

interface EventDetailEntity : JpaRepository<EventDetailEntity, Long> {
}