package plain.bookmaru.domain.lending.port.`in`.command

data class LendingCommand(
    val username : String,
    val bookAffiliationId : Long
)
