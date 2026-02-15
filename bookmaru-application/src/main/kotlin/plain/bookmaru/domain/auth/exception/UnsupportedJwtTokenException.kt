package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class UnsupportedJwtTokenException(value: String) : BaseException(AuthErrorCode.UNSUPPORTED_JWT_TOKEN, value) {
}