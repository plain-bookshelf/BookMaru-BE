package plain.bookmaru.domain.member.vo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class Email(
    @JsonValue
    val email : String?
) {
    init {
    if (!email.isNullOrBlank() && email != "null") {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            require(email.matches(emailRegex)) { "유효하지 않은 이메일 형식입니다: $email" }
        }
    }

    companion object {
        @JvmStatic
        @JsonCreator
        fun from(value: String?): Email = Email(value)
    }
}