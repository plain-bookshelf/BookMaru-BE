    package plain.bookmaru.domain.auth.port.out

    import java.util.Date

    interface SecurityPort {
        fun isPasswordMatch(rawPassword: String, newPassword: String): Boolean
        fun passwordEncode(rawPassword: String): String
        fun getExpiration(accessToken: String): Date
        fun getUsername(accessToken: String): String
    }