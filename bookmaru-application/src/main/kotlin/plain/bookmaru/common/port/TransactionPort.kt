package plain.bookmaru.common.port

interface TransactionPort {
    suspend fun<T> withReadOnly(block: suspend () -> T): T
    suspend fun<T> withTransaction(block: suspend () -> T): T
}