package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class PasswordNotMatchException(value: String) : BaseException(AuthErrorCode.PASSWORD_NOT_MATCH, value) {
}