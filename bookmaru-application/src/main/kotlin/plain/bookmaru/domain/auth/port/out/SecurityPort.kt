    package plain.bookmaru.domain.auth.port.out

    import plain.bookmaru.domain.auth.vo.OAuthProvider
    import java.util.Date

    interface SecurityPort {
        suspend fun isPasswordMatch(rawPassword: String, newPassword: String): Boolean
        suspend fun passwordEncode(rawPassword: String): String
        fun getExpiration(token: String): Date
        fun getOAuthProvider(token: String): OAuthProvider
    }