package plain.bookmaru.common.error

open class BaseException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)