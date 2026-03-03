package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class UnsupportedOAuth2Exception(value: String) : BaseException(AuthErrorCode.UNSUPPORTED_OAUTH2, value) {
}