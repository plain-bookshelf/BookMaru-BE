package plain.bookmaru.common.error

interface ErrorCode {
    val status: CustomHttpStatus
    val code: String
    val message: String
}