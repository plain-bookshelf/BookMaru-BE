package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class UnsupportedOAuth2Exception(value: String) : BaseException(AuthBaseErrorCode.UNSUPPORTED_OAUTH2, value) {
}