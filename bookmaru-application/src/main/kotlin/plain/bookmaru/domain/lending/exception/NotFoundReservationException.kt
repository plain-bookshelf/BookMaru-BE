package plain.bookmaru.domain.lending.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.lending.exception.errorcode.LendingErrorCode

class NotFoundReservationException(value: String) : BaseException(LendingErrorCode.NOT_FOUND_RESERVATION, value)
