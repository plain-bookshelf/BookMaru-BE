package plain.bookmaru.domain.auth.exception.errorcode

import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.error.BaseErrorCode

enum class AuthBaseErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    EXPIRED_JWT_TOKEN(CustomHttpStatus.UNAUTHORIZED, "AUTH-001", "토큰이 만료 되었습니다."),
    UNSUPPORTED_JWT_TOKEN(CustomHttpStatus.UNAUTHORIZED, "AUTH-002", "지원하지 않는 토큰입니다."),
    PASSWORD_NOT_MATCH(CustomHttpStatus.BAD_REQUEST, "AUTH-003", "비밀번호가 일치하지 않습니다."),
    NOT_CORRECT_JWT_TOKEN(CustomHttpStatus.UNAUTHORIZED, "AUTH-004", "옳지 않은 토큰 형식입니다."),
    NOT_FOUND_AUTHENTICATION(CustomHttpStatus.NOT_FOUND, "AUTH-005", "로그인된 유저 정보를 찾지 못 했습니다."),
    ALREADY_CONNECTED_OTHER_OAUTH(CustomHttpStatus.FORBIDDEN, "AUTH-006", "이미 다른 소셜 계정과 연동 되어있습니다."),
    AUTH_SESSION_EXPIRED(CustomHttpStatus.NOT_FOUND, "AUTH-007", "인증 세션 정보가 만료됬거나, 찾지 못 했습니다."),
    UNSUPPORTED_OAUTH2(CustomHttpStatus.BAD_REQUEST, "AUTH-008", "지원하지 않는 소셜 로그인 정보입니다."),
    NOT_MATCH_PLATFORM_INFO(CustomHttpStatus.BAD_REQUEST, "AUTH-009", "platform 정보가 일치하지 않습니다.")
}