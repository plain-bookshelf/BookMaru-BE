package plain.bookmaru.global.error

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import plain.bookmaru.common.error.BaseException
import plain.bookmaru.common.error.BaseErrorCode
import plain.bookmaru.global.error.response.ErrorResponse

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    private fun storeException(e: Exception, request: HttpServletRequest) {
        request.setAttribute("interceptedException", e)
    }

    private fun requestPath(request: HttpServletRequest): String {
        return request.requestURI
    }

    @ExceptionHandler(BaseException::class)
    fun handlerBaseException(e: BaseException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        storeException(e, request)

        val baseErrorCode : BaseErrorCode = e.baseErrorCode

        log.error(e) { "Error Code: [${baseErrorCode.code}], Error Message: [{${baseErrorCode.message}}], Details: [${e.details}], Path: ${requestPath(request)}" }

        return ResponseEntity
            .status(baseErrorCode.status.value)
            .body(ErrorResponse(baseErrorCode.code, baseErrorCode.message, baseErrorCode.status.value, requestPath(request)))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerMethodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        storeException(e, request)
        log.error(e) { "MethodArgumentNotValidException: ${e.message}, Cause: ${e.cause}, Path: ${requestPath(request)}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(ErrorResponse("VALID_ERROR", "유효하지 않은 값이 들어왔습니다.", HttpStatus.BAD_REQUEST.value(), requestPath(request)))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handlerIllegalArgumentException(e: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        storeException(e, request)
        log.error(e) { "IllegalArgumentException: ${e.message}, Cause: ${e.cause}, Path: ${requestPath(request)}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(ErrorResponse("ILLEGAL_ARGUMENT_ERROR", "유효하지 않은 값이 들어왔습니다.", HttpStatus.BAD_REQUEST.value(), requestPath(request)))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException::class)
    fun handlerIllegalStateException(e: IllegalStateException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        storeException(e, request)
        log.error(e) { "IllegalStateException: ${e.message}, Cause: ${e.cause}, Path: ${requestPath(request)}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(ErrorResponse("ILLEGAL_STATE_ERROR", e.message ?: "객체 상태가 정상적이지 않습니다.", HttpStatus.BAD_REQUEST.value(), requestPath(request)))
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handlerException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        storeException(e, request)
        log.error(e) { "InternalServerException: ${e.message}, Cause: ${e.cause}, Path: ${requestPath(request)}" }

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(ErrorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value(), requestPath(request)))
    }
}