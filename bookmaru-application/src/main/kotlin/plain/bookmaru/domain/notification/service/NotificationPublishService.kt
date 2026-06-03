package plain.bookmaru.domain.notification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.port.`in`.PublishNotificationUseCase
import plain.bookmaru.domain.notification.port.out.NotificationPort
import plain.bookmaru.domain.notification.port.out.NotificationPushPort
import plain.bookmaru.domain.notification.port.out.NotificationRealtimePort
import plain.bookmaru.domain.notification.scope.NotificationCoroutineScope

private val log = KotlinLogging.logger {}

@Service
class NotificationPublishService(
    private val notificationPort: NotificationPort,
    private val notificationRealtimePort: NotificationRealtimePort,
    private val notificationPushPort: NotificationPushPort,
    private val notificationCoroutineScope: NotificationCoroutineScope,
    private val transactionPort: TransactionPort
) : PublishNotificationUseCase {

    override suspend fun execute(notification: Notification): Notification {
        val savedNotification = transactionPort.withTransaction {
            notificationPort.save(notification)
        }

        notificationCoroutineScope.launch {
            dispatchNotification(savedNotification)
        }

        return savedNotification
    }

    override suspend fun executeAll(notifications: List<Notification>): List<Notification> {
        if (notifications.isEmpty()) return emptyList()

        val savedNotifications = transactionPort.withTransaction {
            notifications.map(notificationPort::save)
        }

        notificationCoroutineScope.launch {
            coroutineScope {
                savedNotifications.map {
                    async { dispatchNotification(it) }
                }.awaitAll()
            }
        }

        return savedNotifications
    }

    private suspend fun dispatchNotification(notification: Notification) {
        runCatching {
            coroutineScope {
                listOf(
                    async { notificationRealtimePort.send(notification) },
                    async { notificationPushPort.send(notification) }
                ).awaitAll()
            }
        }.onFailure {
            log.warn(it) { "알림 전송에 실패했습니다. notificationId=${notification.id}, memberId=${notification.memberId}" }
        }
    }
}
