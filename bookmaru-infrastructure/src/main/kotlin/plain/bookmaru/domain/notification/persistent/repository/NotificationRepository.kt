package plain.bookmaru.domain.notification.persistent.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.notification.persistent.entity.NotificationEntity
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.domain.notification.vo.TargetType

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findByMemberEntityIdOrderByIdDesc(memberId: Long, pageable: Pageable): List<NotificationEntity>
    fun existsByMemberEntityIdAndTargetIdAndTargetTypeAndType(
        memberId: Long,
        targetId: Long,
        targetType: TargetType,
        type: NotificationType
    ): Boolean
}
