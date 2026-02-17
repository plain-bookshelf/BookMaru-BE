package plain.bookmaru.global.error

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.global.error.response.ErrorResponse

@RestController
@RequestMapping("/error")
class GlobalErrorController : ErrorController {

    @RequestMapping
    fun handlerError(request : HttpServletRequest) : ResponseEntity<ErrorResponse> {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
            ?.toString()?.toIntOrNull() ?: 500

        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse("UNAUTHORIZED", "유저 정보가 잘못 되었습니다", HttpStatus.UNAUTHORIZED.value(), request.requestURI))
        }

        if (status == HttpStatus.FORBIDDEN.value()) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse("FORBIDDEN", "이 리소스에 접근할 권한이 부족합니다", HttpStatus.FORBIDDEN.value(), request.requestURI, ))
        }

        if (status == HttpStatus.NOT_FOUND.value()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse("NOT_FOUND", "요청한 경로를 찾을 수 없음", HttpStatus.NOT_FOUND.value(), request.requestURI))
        }

        return ResponseEntity
            .status(status)
            .body(ErrorResponse("SERVLET_ERROR", "서블릿 에러가 발생했습니다", status, request.requestURI))
    }
}