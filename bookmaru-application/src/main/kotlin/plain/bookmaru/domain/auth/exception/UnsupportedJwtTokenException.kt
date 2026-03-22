package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class UnsupportedJwtTokenException(value: String) : BaseException(AuthBaseErrorCode.UNSUPPORTED_JWT_TOKEN, value) {
}