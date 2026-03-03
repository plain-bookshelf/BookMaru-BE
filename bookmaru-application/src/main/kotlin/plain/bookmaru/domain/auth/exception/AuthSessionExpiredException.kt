package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class AuthSessionExpiredException(value: String) : BaseException(AuthErrorCode.AUTH_SESSION_EXPIRED, value) {
}