package plain.bookmaru.domain.lending.exception.errorcode

import plain.bookmaru.common.error.BaseErrorCode
import plain.bookmaru.common.error.CustomHttpStatus

enum class LendingErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
): BaseErrorCode {
    NOT_EXIST_BOOK_DETAIL(CustomHttpStatus.NOT_FOUND, "LENDING-001", "대여할 수 있는 책이 없습니다."),
    NO_MORE_RENTAL(CustomHttpStatus.CONFLICT, "LENDING-002", "더 이상 대여할 수 없습니다."),
    NO_MORE_RESERVATION(CustomHttpStatus.CONFLICT, "LENDING-003", "더 이상 예약할 수 없습니다.")
}