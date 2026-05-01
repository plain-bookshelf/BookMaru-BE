package plain.bookmaru.global.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import plain.bookmaru.domain.display.scope.CacheCoroutineScope
import plain.bookmaru.domain.notification.scope.NotificationCoroutineScope
import plain.bookmaru.domain.verification.scope.MailCoroutineScope
import java.util.concurrent.Executors

@Configuration
class CoroutineConfig {

    @Bean("virtualDispatcher")
    fun virtualThreadPool(): CoroutineDispatcher =
        Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    @Bean
    fun mailCoroutineScope(@Qualifier("virtualDispatcher") virtualDispatcher: CoroutineDispatcher): MailCoroutineScope {
        val scope = CoroutineScope(SupervisorJob() + virtualDispatcher)
        return MailCoroutineScope(scope)
    }

    @Bean
    fun cacheCoroutineScope(@Qualifier("virtualDispatcher") virtualDispatcher: CoroutineDispatcher): CacheCoroutineScope {
        val scope = CoroutineScope(SupervisorJob() + virtualDispatcher)
        return CacheCoroutineScope(scope)
    }

    @Bean
    fun notificationCoroutineScope(@Qualifier("virtualDispatcher") virtualDispatcher: CoroutineDispatcher): NotificationCoroutineScope {
        val scope = CoroutineScope(SupervisorJob() + virtualDispatcher)
        return NotificationCoroutineScope(scope)
    }
}
