package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class ExpiredJwtTokenException(value: String) : BaseException(AuthBaseErrorCode.EXPIRED_JWT_TOKEN, value) {
}