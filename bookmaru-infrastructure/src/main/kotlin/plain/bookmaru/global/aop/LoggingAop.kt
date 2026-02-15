package plain.bookmaru.global.aop

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}

@Aspect
@Component
class LoggingAop {

    @Around("@annotation(plain.bookmaru.common.annotation.LogExecution)")
    fun logExecution(joinPoint: ProceedingJoinPoint): Any? {
        val stopWatch = StopWatch()
        val methodName = joinPoint.signature.name
        val className = joinPoint.signature.javaClass.simpleName

        val startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        log.info {"[START] [$className.$methodName] - 시작 시간: $startTime"}

        stopWatch.start()

        try {
            val result = joinPoint.proceed()

            stopWatch.stop()
            val endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            log.info {"[SUCCESS] [$className.$methodName] - 종료 시간: $endTime (소요 시간: ${stopWatch.totalTimeMillis}ms)"}

            return result
        } catch (e: Exception) {
            stopWatch.stop()
            log.error {"[FAIL] [$className.$methodName] - 에러 발생: ${e.message} (소요 시간: ${stopWatch.totalTimeMillis}ms)"}
            throw e
        }
    }
}