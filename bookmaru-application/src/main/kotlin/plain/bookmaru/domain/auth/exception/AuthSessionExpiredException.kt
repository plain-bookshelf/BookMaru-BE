package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class AuthSessionExpiredException(value: String) : BaseException(AuthBaseErrorCode.AUTH_SESSION_EXPIRED, value) {
}