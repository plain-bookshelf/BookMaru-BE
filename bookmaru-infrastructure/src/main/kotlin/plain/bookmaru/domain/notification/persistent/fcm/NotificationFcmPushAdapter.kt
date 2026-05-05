package plain.bookmaru.domain.notification.persistent.fcm

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.persistent.repository.MemberDeviceRepository
import plain.bookmaru.domain.notification.model.Notification
import plain.bookmaru.domain.notification.port.out.NotificationPushPort

private val log = KotlinLogging.logger {}

@Component
class NotificationFcmPushAdapter(
    private val firebaseMessaging: FirebaseMessaging,
    private val objectMapper: ObjectMapper,
    private val memberDeviceRepository: MemberDeviceRepository
) : NotificationPushPort {

    override fun send(notification: Notification) {
        val memberId = notification.memberId
        val deviceTokens = memberDeviceRepository.findAllByMemberEntityId(memberId)
            .map { it.deviceToken }
            .distinct()

        if (deviceTokens.isEmpty()) return

        val data = mapOf(
            "notificationId" to (notification.id?.toString() ?: ""),
            "memberId" to memberId.toString(),
            "name" to notification.notificationInfo.name,
            "type" to notification.notificationInfo.type.name,
            "url" to notification.notificationInfo.url,
            "targetId" to notification.targetInfo.targetId.toString(),
            "targetType" to notification.targetInfo.targetType.name,
            "payload" to objectMapper.writeValueAsString(notification.notificationInfo.payload)
        )

        deviceTokens.chunked(500).forEach { tokenChunk ->
            runCatching {
                firebaseMessaging.sendEachForMulticast(
                    MulticastMessage.builder()
                        .putAllData(data)
                        .addAllTokens(tokenChunk)
                        .build()
                )
            }.onFailure {
                log.warn(it) { "FCM 알림 전송에 실패했습니다. memberId=$memberId, tokenCount=${tokenChunk.size}" }
            }
        }
    }
}
