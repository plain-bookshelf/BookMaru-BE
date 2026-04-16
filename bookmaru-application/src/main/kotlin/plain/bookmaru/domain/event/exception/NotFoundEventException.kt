package plain.bookmaru.domain.event.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.event.exception.errorcode.EventErrorCode

class NotFoundEventException(value: String) : BaseException(EventErrorCode.NOT_FOUND_EVENT, value)