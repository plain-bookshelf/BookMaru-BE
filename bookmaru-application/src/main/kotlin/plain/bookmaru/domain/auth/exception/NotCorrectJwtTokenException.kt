package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class NotCorrectJwtTokenException(value: String) : BaseException(AuthErrorCode.NOT_CORRECT_JWT_TOKEN, value) {
}