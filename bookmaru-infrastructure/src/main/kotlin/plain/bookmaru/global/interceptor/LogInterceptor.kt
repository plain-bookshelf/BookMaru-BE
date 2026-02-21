package plain.bookmaru.global.interceptor

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.AsyncHandlerInterceptor

private val log = KotlinLogging.logger {}

@Component
class LogInterceptor : AsyncHandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setAttribute("startTime", System.currentTimeMillis())
        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        val start = request.getAttribute("startTime") as? Long ?: return
        val end = System.currentTimeMillis()
        val duration = end - start

        var handlerInfo = request.requestURI
        if (handler is HandlerMethod) {
            val className = handler.beanType.simpleName
            val methodName = handler.method.name
            handlerInfo = "$className.$methodName"
        }

        val status = response.status
        val cause = ex?.cause
        val message = ex?.message

        if (ex != null || status >= 400) {
            log.error { "[FAIL] $handlerInfo - Status: $status - Cause: $cause - Message: $message (소요 시간: $duration ms)" }
        } else {
            log.info { "[SUCCESS] $handlerInfo - Status: $status (소요 시간: $duration ms)" }
        }
    }
}