    package plain.bookmaru.domain.auth.port.out

    interface SecurityPort {
        fun isPasswordMatch(rawPassword: String, newPassword: String): Boolean
        fun passwordEncode(rawPassword: String): String
    }