package plain.bookmaru.domain.notification.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.persistent.entity.NotificationEntity
import plain.bookmaru.domain.notification.vo.NotificationInfo
import plain.bookmaru.domain.notification.vo.TargetInfo

@Component
class NotificationMapper {

    fun toDomain(entity: NotificationEntity) : Notification {
        return Notification(
            id = entity.id,
            memberId = entity.memberEntity.id,
            targetInfo = TargetInfo(
                targetId = entity.targetId,
                notificationType = entity.targetType,
            ),
            notificationInfo = NotificationInfo(
                name = entity.name,
                payload = entity.payload,
                type = entity.type,
                url = entity.url,
            ),
            isRead = entity.isRead
        )
    }

    fun toEntity(domain: Notification, memberEntity: MemberEntity): NotificationEntity {
        return NotificationEntity(
            memberEntity = memberEntity,
            targetId = domain.targetInfo.targetId,
            targetType = domain.targetInfo.notificationType,
            name = domain.notificationInfo.name,
            payload = domain.notificationInfo.payload,
            type = domain.notificationInfo.type,
            url = domain.notificationInfo.url
        ).apply {
            this.isRead = domain.isRead
        }
    }
}