package plain.bookmaru.common.error

interface ErrorCode {
    val name: String
    val message: String
    val status: CustomHttpStatus
}