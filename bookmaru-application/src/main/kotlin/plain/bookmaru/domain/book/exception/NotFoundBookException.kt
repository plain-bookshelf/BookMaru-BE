package plain.bookmaru.domain.book.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.book.exception.errorcode.BookErrorCode

class NotFoundBookException(value: String) : BaseException(BookErrorCode.NOT_FOUND_BOOK, value)