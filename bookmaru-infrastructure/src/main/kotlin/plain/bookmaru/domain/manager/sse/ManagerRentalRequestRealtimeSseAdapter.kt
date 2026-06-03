package plain.bookmaru.domain.manager.sse

import org.springframework.stereotype.Component
import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult
import plain.bookmaru.domain.manager.port.out.RentalRequestRealtimePort

@Component
class ManagerRentalRequestRealtimeSseAdapter(
    private val managerRentalRequestEmitterManager: ManagerRentalRequestEmitterManager
) : RentalRequestRealtimePort {

    override fun send(affiliationId: Long, requests: List<RentalRequestCheckResult>) {
        managerRentalRequestEmitterManager.sendToAffiliation(
            affiliationId = affiliationId,
            eventName = "rental-request-update",
            data = RentalRequestSseResponse(requests)
        )
    }
}
