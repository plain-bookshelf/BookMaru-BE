package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class PasswordNotMatchException(value: String) : BaseException(AuthBaseErrorCode.PASSWORD_NOT_MATCH, value) {
}