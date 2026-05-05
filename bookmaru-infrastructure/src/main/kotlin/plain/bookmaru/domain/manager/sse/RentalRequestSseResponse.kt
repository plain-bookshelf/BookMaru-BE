package plain.bookmaru.domain.manager.sse

import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult

data class RentalRequestSseResponse(
    val requests: List<RentalRequestCheckResult>
)
