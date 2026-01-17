package plain.bookmaruinfrastructure.global.error

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaruinfrastructure.global.error.response.ErrorResponse

@RestController
@RequestMapping("/error")
class GlobalErrorController {

    @RequestMapping
    fun handlerError(request : HttpServletRequest) : ResponseEntity<ErrorResponse> {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
            ?.toString()?.toIntOrNull() ?: 500

        if (status == HttpStatus.NOT_FOUND.value()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse("NOT_FOUND", "요청한 경로를 찾을 수 없음", HttpStatus.NOT_FOUND.value()))
        }

        return ResponseEntity
            .status(status)
            .body(ErrorResponse("SERVLET_ERROR", "서블릿 에러가 발생했습니다", status))
    }
}