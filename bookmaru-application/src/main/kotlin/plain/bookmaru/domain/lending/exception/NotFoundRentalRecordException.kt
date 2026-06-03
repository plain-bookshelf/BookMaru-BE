package plain.bookmaru.domain.lending.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.lending.exception.errorcode.LendingErrorCode

class NotFoundRentalRecordException(value: String) : BaseException(LendingErrorCode.NOT_FOUND_RENTAL_RECORD, value)