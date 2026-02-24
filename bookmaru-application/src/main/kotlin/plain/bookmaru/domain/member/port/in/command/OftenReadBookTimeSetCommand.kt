package plain.bookmaru.domain.member.port.`in`.command

import java.time.LocalDateTime

data class OftenReadBookTimeSetCommand(
    val time: LocalDateTime,
    val username: String
)
