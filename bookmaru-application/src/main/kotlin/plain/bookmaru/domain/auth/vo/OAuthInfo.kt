package plain.bookmaru.domain.auth.vo

data class OAuthInfo(
    val provider: OAuthProvider,
    val providerId: String
)