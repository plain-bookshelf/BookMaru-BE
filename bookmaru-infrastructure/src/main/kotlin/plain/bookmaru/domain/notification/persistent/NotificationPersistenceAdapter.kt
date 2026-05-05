package plain.bookmaru.domain.notification.persistent

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.persistent.mapper.NotificationMapper
import plain.bookmaru.domain.notification.persistent.repository.NotificationRepository
import plain.bookmaru.domain.notification.port.out.NotificationPort
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.domain.notification.vo.TargetType
import plain.bookmaru.global.config.DbProtection

@Component
class NotificationPersistenceAdapter(
    private val notificationRepository: NotificationRepository,
    private val notificationMapper: NotificationMapper,
    private val memberRepository: MemberRepository,
    private val dbProtection: DbProtection
) : NotificationPort {

    override fun save(notification: Notification): Notification {
        val memberId = notification.memberId

        val memberProxy = memberRepository.getReferenceById(memberId)
        val entity = notificationMapper.toEntity(notification, memberProxy)
        val savedEntity = notificationRepository.save(entity)

        return notificationMapper.toDomain(savedEntity)
    }

    override suspend fun findRecentByMemberId(memberId: Long, limit: Int): List<Notification> = dbProtection.withReadOnly {
        val pageable = PageRequest.of(0, limit)
        val notifications = notificationRepository.findByMemberEntityIdOrderByIdDesc(memberId, pageable)

        return@withReadOnly notificationMapper.toDomainList(notifications)
    }

    override suspend fun findByMemberId(
        memberId: Long,
        pageCommand: PageCommand
    ): SliceResult<Notification> = dbProtection.withReadOnly {
        val pageable = PageRequest.of(pageCommand.page, pageCommand.size + 1)
        val notifications = notificationRepository.findByMemberEntityIdOrderByIdDesc(memberId, pageable)
            .map(notificationMapper::toDomain)

        return@withReadOnly sliceResult(notifications, pageCommand.size)
    }

    override suspend fun existsByMemberAndTargetAndType(
        memberId: Long,
        targetId: Long,
        targetType: TargetType,
        type: NotificationType
    ): Boolean = dbProtection.withReadOnly {
        notificationRepository.existsByMemberEntityIdAndTargetIdAndTargetTypeAndType(
            memberId = memberId,
            targetId = targetId,
            targetType = targetType,
            type = type
        )
    }

    private fun <T> sliceResult(results: List<T>, requestSize: Int): SliceResult<T> {
        val hasNext = results.size > requestSize
        val content = if (hasNext) results.dropLast(1) else results

        return SliceResult(
            content = content,
            isLastPage = !hasNext
        )
    }
}
