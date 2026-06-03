package plain.bookmaru.common.error

interface BaseErrorCode {
    val status: CustomHttpStatus
    val code: String
    val message: String
}