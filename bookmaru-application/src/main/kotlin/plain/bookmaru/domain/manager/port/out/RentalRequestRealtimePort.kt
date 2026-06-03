package plain.bookmaru.domain.manager.port.out

import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult

interface RentalRequestRealtimePort {
    fun send(affiliationId: Long, requests: List<RentalRequestCheckResult>)
}
