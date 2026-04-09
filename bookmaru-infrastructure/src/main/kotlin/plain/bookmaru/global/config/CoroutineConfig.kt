package plain.bookmaru.global.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import plain.bookmaru.domain.verification.scope.MailCoroutineScope
import java.util.concurrent.Executors

@Configuration
class CoroutineConfig {

    @Bean("virtualDispatcher")
    fun virtualThreadPool(): CoroutineDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    @Bean
    fun mailCoroutineScope() : MailCoroutineScope {
        val originalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        return MailCoroutineScope(originalScope)
    }
}