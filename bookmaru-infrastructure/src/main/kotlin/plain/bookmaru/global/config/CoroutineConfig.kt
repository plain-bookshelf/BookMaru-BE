package plain.bookmaru.global.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import plain.bookmaru.domain.verificationcode.scope.MailCoroutineScope

@Configuration
class CoroutineConfig {

    @Bean
    fun mailCoroutineScope() : MailCoroutineScope {
        val originalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        return MailCoroutineScope(originalScope)
    }
}