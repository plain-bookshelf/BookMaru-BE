package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class NotFoundAuthenticationException(value: String) : BaseException(AuthErrorCode.NOT_FOUND_AUTHENTICATION, value) {
}