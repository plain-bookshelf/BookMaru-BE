package plain.bookmaru.domain.lending.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.lending.exception.errorcode.LendingErrorCode

class OverdueException(value: String) : BaseException(LendingErrorCode.OVERDUE, value) {
}