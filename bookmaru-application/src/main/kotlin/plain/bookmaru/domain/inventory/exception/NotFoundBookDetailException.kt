package plain.bookmaru.domain.inventory.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.inventory.exception.errorcode.InventoryErrorCode

class NotFoundBookDetailException(value: String) : BaseException(InventoryErrorCode.NOT_FOUND_BOOK_DETAIL, value)