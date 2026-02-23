package plain.bookmaru.global.config

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Executors

private val VirtualDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
private val DbSemaphore = Semaphore(30)

@Component
class DbProtection(
    transactionManager: PlatformTransactionManager,
) {
    private val modifyTemplate = TransactionTemplate(transactionManager)

    private val readOnlyTemplate = TransactionTemplate(transactionManager).apply {
        isReadOnly = true
    }

    suspend fun <T> withReadOnly(block: () -> T): T {
        return withContext(VirtualDispatcher) {
            withTimeout(5000L) {
                DbSemaphore.withPermit {
                    @Suppress("UNCHECKED_CAST")
                    readOnlyTemplate.execute {
                        block()
                    } as T
                }
            }
        }
    }

    suspend fun <T> withTransaction(block: () -> T): T {
        return withContext(VirtualDispatcher) {
            withTimeout(5000L) {
                DbSemaphore.withPermit {
                    modifyTemplate.execute {
                        try {
                            block()
                        } catch (e: Exception) {
                            it.setRollbackOnly()
                            throw e
                        }
                    } ?: throw IllegalStateException("트랜잭션 결과가 null 일 수 없습니다.")
                }
            }
        }
    }
}