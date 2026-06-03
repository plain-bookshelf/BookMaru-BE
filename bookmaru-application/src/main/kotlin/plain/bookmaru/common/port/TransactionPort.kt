package plain.bookmaru.common.port

interface TransactionPort {
    suspend fun<T> withReadOnly(block: () -> T): T
    suspend fun<T> withTransaction(block: () -> T): T
}