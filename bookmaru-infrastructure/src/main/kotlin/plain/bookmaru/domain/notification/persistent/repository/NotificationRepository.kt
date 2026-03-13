package plain.bookmaru.domain.notification.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.notification.persistent.entity.NotificationEntity

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
}