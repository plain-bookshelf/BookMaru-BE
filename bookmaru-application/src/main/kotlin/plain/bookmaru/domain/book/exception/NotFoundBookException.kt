package plain.bookmaru.domain.book.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.book.exception.errorcode.BookBaseErrorCode

class NotFoundBookException(value: String) : BaseException(BookBaseErrorCode.NOT_FOUND_BOOK, value)