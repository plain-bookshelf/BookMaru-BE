package plain.bookmaru.domain.notification.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.domain.notification.vo.TargetType

interface NotificationPort {
    fun save(notification: Notification): Notification
    suspend fun findRecentByMemberId(memberId: Long, limit: Int): List<Notification>
    suspend fun findByMemberId(memberId: Long, pageCommand: PageCommand): SliceResult<Notification>
    suspend fun existsByMemberAndTargetAndType(
        memberId: Long,
        targetId: Long,
        targetType: TargetType,
        type: NotificationType
    ): Boolean
}
