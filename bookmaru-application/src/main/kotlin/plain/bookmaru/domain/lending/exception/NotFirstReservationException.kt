package plain.bookmaru.domain.lending.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.lending.exception.errorcode.LendingErrorCode

class NotFirstReservationException(value: String) : BaseException(LendingErrorCode.NOT_FIRST_RESERVATION, value)
