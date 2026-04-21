package plain.bookmaru.global.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream

private val log = KotlinLogging.logger {}

@Configuration
class FirebaseConfig {

    @Value("\${firebase.json-content}")
    private lateinit var jsonContent: String

    @PostConstruct
    fun init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                val stream = ByteArrayInputStream(jsonContent.toByteArray())
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build()

                FirebaseApp.initializeApp(options)
                log.info { "FCM 앱이 성공적으로 초기화 되었습니다." }
            }
        } catch (e: Exception) {
            log.error(e) { "FCM 앱이 초기화 중 에러가 발생했습니다." }
        }

        @Bean
        fun firebaseMessaging(): FirebaseMessaging {
            return FirebaseMessaging.getInstance()
        }
    }
}