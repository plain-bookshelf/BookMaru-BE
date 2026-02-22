package plain.bookmaru.domain.verification.presentation.dto.request

import plain.bookmaru.domain.verification.port.`in`.command.SendVerificationCodeCommand
import plain.bookmaru.domain.verification.vo.VerificationCodeType

data class SendEmailRequestDto(
    val email : String
) {
    fun toCommand(codeType: String)
    = SendVerificationCodeCommand(email, VerificationCodeType.valueOf(codeType))
}