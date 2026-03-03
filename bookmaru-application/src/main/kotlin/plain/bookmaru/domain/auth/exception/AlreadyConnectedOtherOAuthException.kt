package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class AlreadyConnectedOtherOAuthException(value: String) : BaseException(AuthErrorCode.ALREADY_CONNECTED_OTHER_OAUTH, value) {
}