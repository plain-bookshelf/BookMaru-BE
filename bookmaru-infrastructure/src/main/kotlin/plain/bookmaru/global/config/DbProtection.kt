package plain.bookmaru.global.config

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import plain.bookmaru.common.port.TransactionPort

@Component
class DbProtection(
    transactionManager: PlatformTransactionManager
) : TransactionPort {
    private val modifyTemplate = TransactionTemplate(transactionManager)

    private val readOnlyTemplate = TransactionTemplate(transactionManager).apply {
        isReadOnly = true
    }

    override suspend fun <T> withReadOnly(block: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return readOnlyTemplate.execute { block() } as T
    }

    override suspend fun <T> withTransaction(block: () -> T): T {
        return modifyTemplate.execute {
            try {
                block()
            } catch (e: Exception) {
                it.setRollbackOnly()
                throw e
            }
        } ?: throw IllegalStateException("Transaction returned null unexpectedly.")
    }
}
