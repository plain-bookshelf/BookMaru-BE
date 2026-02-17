package plain.bookmaru.global.aop

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}

@Component
@Aspect
class LoggingAop {

    @Before("@annotation(plain.bookmaru.common.annotation.LogExecution)")
    fun logExecution(joinPoint: JoinPoint) {
        val methodName = joinPoint.signature.name
        val className = joinPoint.signature.declaringType.simpleName
        val args = joinPoint.args.joinToString(", ") { it.toString() }

        val startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        log.info {"[REQUEST] [$className.$methodName] Args: $args - 시작 시간: $startTime"}
    }
}