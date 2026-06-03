package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.`in`.ViewMainPageEventUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageEventCommand
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.ViewMainPageEventResult
import plain.bookmaru.domain.event.port.out.EventPort

private val log = KotlinLogging.logger {}

@Service
class ViewMainPageEventService(
    private val eventPort: EventPort,
    private val mainPagePort: MainPagePort,
) : ViewMainPageEventUseCase {

    override suspend fun execute(command: ViewMainPageEventCommand): ViewMainPageEventResult {
        val event = eventCacheCheck(command.affiliationId)

        return ViewMainPageEventResult(event)
    }

    private suspend fun eventCacheCheck(affiliationId: Long) : List<EventInfoResult> {
        var cacheEvents = mainPagePort.loadEvents(affiliationId)

        if (cacheEvents == null) {
            log.info { "event cache 정보를 찾지 못 했습니다." }

            val eventList = eventPort.findAll()
                ?: emptyList()
            log.info { "event 정보를 불러오는데 성공했습니다." }

            val eventInfoResult = eventList.map {
                EventInfoResult(imageUrl = it.eventInfo.imageUrl, id = it.id!!) }

            mainPagePort.saveEvents(eventInfoResult, affiliationId)
            log.info { "event 정보를 cache 하는데 성공했습니다." }

            cacheEvents = eventInfoResult
        }
        return cacheEvents
    }
}