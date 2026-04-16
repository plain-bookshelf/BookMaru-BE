package plain.bookmaru.domain.event.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.event.exception.errorcode.EventErrorCode

class NotEventCreateUserException(value: String) : BaseException(EventErrorCode.NOT_EVENT_CREATE_USER, value)