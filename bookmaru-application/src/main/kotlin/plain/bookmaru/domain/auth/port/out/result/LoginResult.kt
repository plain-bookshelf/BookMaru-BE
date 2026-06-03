package plain.bookmaru.domain.auth.port.out.result

sealed class LoginResult {
    data class Success(val tokens: TokenResult) : LoginResult()
    data class NeedMoreInfo(val registerToken: String) : LoginResult()
}