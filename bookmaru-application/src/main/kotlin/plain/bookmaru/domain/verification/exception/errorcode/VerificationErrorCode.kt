package plain.bookmaru.domain.verification.exception.errorcode

import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.error.ErrorCode

enum class VerificationErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
): ErrorCode {
    NOT_FOUND_EMAIL(CustomHttpStatus.NOT_FOUND, "VERIFICATION-001", "이메일을 찾지 못 했습니다."),
    NOT_MATCH_VERIFICATION_CODE(CustomHttpStatus.BAD_REQUEST, "VERIFICATION-002", "인증코드가 틀렸습니다."),
    NOT_MATCH_OFFICIAL_CODE(CustomHttpStatus.BAD_REQUEST, "VERIFICATION-003", "관계자 인증코드가 매치되지 않았습니다."),
    NOT_MATCH_EMAIL_MEMBER(CustomHttpStatus.BAD_REQUEST, "VERIFICATION-004", "유저의 이메일 정보와 일치하지 않습니다.")
}