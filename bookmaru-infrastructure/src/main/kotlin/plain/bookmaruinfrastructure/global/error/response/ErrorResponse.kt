package plain.bookmaruinfrastructure.global.error.response


data class ErrorResponse(
    val code: String,
    val message: String,
    val status: Int
)