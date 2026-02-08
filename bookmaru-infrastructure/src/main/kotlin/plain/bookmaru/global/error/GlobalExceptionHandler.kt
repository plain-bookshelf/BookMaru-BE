package plain.bookmaru.global.error

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import plain.bookmaru.common.error.BaseException
import plain.bookmaru.common.error.ErrorCode
import plain.bookmaru.global.error.response.ErrorResponse

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler : ErrorController {

    @ExceptionHandler(BaseException::class)
    fun handlerBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        val errorCode : ErrorCode = e.errorCode

        log.error {"Error Code: [${errorCode.message}], Error Message: [{${errorCode.message}}], Details: [${e.details}]"}

        return ResponseEntity
            .status(errorCode.status.value)
            .body(ErrorResponse(errorCode.code, errorCode.message, errorCode.status.value))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        log.error { "MethodArgumentNotValidException: ${e.message}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(ErrorResponse("VALID_ERROR", "유효하지 않은 값이 들어왔습니다.", HttpStatus.BAD_REQUEST.value()))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handlerIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.error { "IllegalArgumentException: ${e.message}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST.value())
            .body(ErrorResponse("ILLEGAL_ARGUMENT_ERROR", "유효하지 않은 값이 들어왔습니다.", HttpStatus.BAD_REQUEST.value()))
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handlerException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error { "InternalServerException: ${e.message}" }

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(ErrorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()))
    }
}