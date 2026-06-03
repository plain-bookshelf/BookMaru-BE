package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class NotFoundAuthenticationException(value: String) : BaseException(AuthBaseErrorCode.NOT_FOUND_AUTHENTICATION, value) {
}