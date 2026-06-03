package plain.bookmaru.common.error

open class BaseException(
    val baseErrorCode: BaseErrorCode,
    val details: String?
) : RuntimeException()