package plain.bookmaru.domain.auth.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.auth.exception.errorcode.AuthBaseErrorCode

class NotMatchPlatformInfoException(value: String) : BaseException(AuthBaseErrorCode.NOT_MATCH_PLATFORM_INFO, value) {
}