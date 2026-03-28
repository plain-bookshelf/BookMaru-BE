package plain.bookmaru.domain.lending.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.lending.exception.errorcode.LendingErrorCode

class NotExistBookDetailException(value: String): BaseException(LendingErrorCode.NOT_EXIST_BOOK_DETAIL, value)