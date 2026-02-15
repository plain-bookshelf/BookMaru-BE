package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class ExpiredJwtTokenException(value: String) : BaseException(AuthErrorCode.EXPIRED_JWT_TOKEN, value) {
}