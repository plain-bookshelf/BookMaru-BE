package plain.bookmaru.common.error

open class BaseException(
    val errorCode: ErrorCode,
    val details: String
) : RuntimeException()