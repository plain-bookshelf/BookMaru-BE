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
                log.info { "Firebase 앱 초기화에 성공했습니다." }
            }
        } catch (e: Exception) {
            log.error(e) { "Firebase 앱 초기화에 실패했습니다." }
        }
    }

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }
}
