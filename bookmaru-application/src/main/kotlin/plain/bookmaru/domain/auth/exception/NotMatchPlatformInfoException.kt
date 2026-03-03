package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthErrorCode

class NotMatchPlatformInfoException(value: String) : BaseException(AuthErrorCode.NOT_MATCH_PLATFORM_INFO, value) {
}