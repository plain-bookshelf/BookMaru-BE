package plain.bookmaru.domain.lending.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.lending.exception.errorcode.LendingErrorCode

class NoMoreRentalException(value: String): BaseException(LendingErrorCode.NO_MORE_RENTAL, value) {
}