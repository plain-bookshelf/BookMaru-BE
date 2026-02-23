package plain.bookmaru.domain.verification.presentation.dto.response

import plain.bookmaru.domain.verification.port.out.result.UsernameResult

data class UsernameResponseDto(
    val usernameResult: UsernameResult
)
