package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class AlreadyConnectedOtherOAuthException(value: String) : BaseException(AuthBaseErrorCode.ALREADY_CONNECTED_OTHER_OAUTH, value) {
}