package plain.bookmaru.domain.lending.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.lending.exception.errorcode.LendingErrorCode

class CannotReserveAvailableBookException(value: String) :
    BaseException(LendingErrorCode.CANNOT_RESERVE_AVAILABLE_BOOK, value)
