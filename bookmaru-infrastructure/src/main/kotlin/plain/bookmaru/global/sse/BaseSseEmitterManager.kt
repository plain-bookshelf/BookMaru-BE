package plain.bookmaru.global.sse

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong

private val log = KotlinLogging.logger {}

abstract class BaseSseEmitterManager(
    private val channelName: String,
    private val targetLabel: String
) {

    companion object {
        private const val DEFAULT_TIMEOUT = 60L * 60L * 1000L
        private const val MAX_EVENT_CACHE_SIZE = 100
        private const val CONNECT_EVENT_NAME = "sse-connect"
    }

    private val eventSequence = AtomicLong(0L)
    private val emitters = ConcurrentHashMap<Long, ConcurrentHashMap<String, SseEmitter>>()
    private val eventCache = ConcurrentHashMap<Long, ConcurrentSkipListMap<Long, SseEventMessage>>()

    protected fun subscribeInternal(targetId: Long, lastEventId: String?): SseEmitter {
        val emitterId = createEmitterId(targetId)
        val emitter = SseEmitter(DEFAULT_TIMEOUT)

        emitters.computeIfAbsent(targetId) { ConcurrentHashMap() }[emitterId] = emitter
        attachEmitterLifecycle(targetId, emitterId, emitter)

        sendToEmitter(emitter, CONNECT_EVENT_NAME, SseConnectResponse())
        replayMissedEvents(targetId, lastEventId, emitter)

        log.info { "$channelName 연결. $targetLabel=$targetId, emitterId=$emitterId" }
        return emitter
    }

    protected fun sendToTargetInternal(targetId: Long, eventName: String, data: Any) {
        val targetEmitters = emitters[targetId]
        if (targetEmitters.isNullOrEmpty()) return

        val event = createAndCacheEvent(targetId, eventName, data)

        targetEmitters.forEach { (emitterId, emitter) ->
            val sent = sendEvent(emitter, event)
            if (!sent) {
                removeEmitter(targetId, emitterId)
            }
        }
    }

    fun sendToEmitter(emitter: SseEmitter, eventName: String, data: Any): Boolean {
        val event = SseEventMessage(
            id = eventSequence.incrementAndGet(),
            eventName = eventName,
            data = data
        )

        return sendEvent(emitter, event)
    }

    private fun replayMissedEvents(targetId: Long, lastEventId: String?, emitter: SseEmitter) {
        val lastReceivedEventId = lastEventId
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.toLongOrNull()
            ?: return

        val cachedEvents = eventCache[targetId]
            ?.tailMap(lastReceivedEventId + 1)
            ?.values
            ?: return

        cachedEvents.forEach { sendEvent(emitter, it) }
    }

    private fun createAndCacheEvent(targetId: Long, eventName: String, data: Any): SseEventMessage {
        val event = SseEventMessage(
            id = eventSequence.incrementAndGet(),
            eventName = eventName,
            data = data
        )

        val targetEventCache = eventCache.computeIfAbsent(targetId) { ConcurrentSkipListMap() }
        targetEventCache[event.id] = event

        while (targetEventCache.size > MAX_EVENT_CACHE_SIZE) {
            targetEventCache.pollFirstEntry()
        }

        return event
    }

    private fun attachEmitterLifecycle(targetId: Long, emitterId: String, emitter: SseEmitter) {
        emitter.onCompletion {
            removeEmitter(targetId, emitterId)
            log.info { "$channelName 연결 종료. $targetLabel=$targetId, emitterId=$emitterId" }
        }

        emitter.onTimeout {
            removeEmitter(targetId, emitterId)
            emitter.complete()
            log.info { "$channelName 타임아웃. $targetLabel=$targetId, emitterId=$emitterId" }
        }

        emitter.onError {
            removeEmitter(targetId, emitterId)
            emitter.completeWithError(it)
            log.warn(it) { "$channelName 오류. $targetLabel=$targetId, emitterId=$emitterId" }
        }
    }

    private fun removeEmitter(targetId: Long, emitterId: String) {
        emitters[targetId]?.remove(emitterId)

        if (emitters[targetId].isNullOrEmpty()) {
            emitters.remove(targetId)
        }
    }

    private fun sendEvent(emitter: SseEmitter, event: SseEventMessage): Boolean {
        return try {
            emitter.send(
                SseEmitter.event()
                    .id(event.id.toString())
                    .name(event.eventName)
                    .data(event.data)
            )
            true
        } catch (e: IOException) {
            log.warn(e) { "$channelName 전송 실패. eventId=${event.id}, eventName=${event.eventName}" }
            false
        } catch (e: IllegalStateException) {
            log.warn(e) { "$channelName 이미 전송 종료. eventId=${event.id}, eventName=${event.eventName}" }
            false
        }
    }

    private fun createEmitterId(targetId: Long): String {
        return "$targetId-${UUID.randomUUID()}"
    }
}
