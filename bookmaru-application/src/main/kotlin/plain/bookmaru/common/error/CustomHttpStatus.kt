package plain.bookmaru.common.error

enum class CustomHttpStatus(val value: Int) {
    /*
    200 ~ 204
     */
    OK(200),
    CREATED(201),
    NO_CONTENT(204),

    /*
    400 ~ 409
     */
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    CONFLICT(409),

    /*
    500
     */
    INTERNAL_SERVER_ERROR(500),
}