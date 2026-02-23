package plain.bookmaru.domain.auth.presentation.dto.response

import plain.bookmaru.domain.auth.port.out.result.TokenResult

data class TokenResponseDto(
    val tokenResult: TokenResult
)
