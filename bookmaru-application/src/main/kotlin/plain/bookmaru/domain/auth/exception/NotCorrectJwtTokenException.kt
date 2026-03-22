package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class NotCorrectJwtTokenException(value: String) : BaseException(AuthBaseErrorCode.NOT_CORRECT_JWT_TOKEN, value) {
}