package plain.bookmaru.domain.book.exception.errorcode

import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.error.ErrorCode

enum class BookErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
) : ErrorCode {
    NOT_FOUND_BOOK(CustomHttpStatus.NOT_FOUND, "BOOK-01", "책 정보를 찾지 못 했습니다.")
}