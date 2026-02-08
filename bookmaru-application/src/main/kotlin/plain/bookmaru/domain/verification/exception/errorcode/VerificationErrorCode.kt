package plain.bookmaru.domain.verification.exception.errorcode

import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.error.ErrorCode

enum class VerificationErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
): ErrorCode {
    NOT_FOUND_EMAIL(CustomHttpStatus.NOT_FOUND, "EMAIL-001", "이메일을 찾지 못 했습니다."),
    NOT_MATCH_VERIFICATION_CODE(CustomHttpStatus.BAD_REQUEST, "EMAIL-002", "인증코드가 틀렸습니다.")
}