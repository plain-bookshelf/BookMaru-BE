package plain.bookmaru.domain.event.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.event.exception.errorcode.EventErrorCode

class NotContainEventContentException : BaseException(EventErrorCode.NOT_CONTAIN_CONTENT, null) {
}