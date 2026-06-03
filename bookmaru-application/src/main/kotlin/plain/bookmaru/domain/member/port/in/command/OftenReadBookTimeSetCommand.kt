package plain.bookmaru.domain.member.port.`in`.command

import java.time.LocalTime

data class OftenReadBookTimeSetCommand(
    val time: LocalTime,
    val username: String
)
