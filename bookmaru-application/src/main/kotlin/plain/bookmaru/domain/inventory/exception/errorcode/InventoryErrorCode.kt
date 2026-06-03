package plain.bookmaru.domain.inventory.exception.errorcode

import plain.bookmaru.common.error.BaseErrorCode
import plain.bookmaru.common.error.CustomHttpStatus

enum class InventoryErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
): BaseErrorCode {
    NOT_FOUND_BOOK_DETAIL(CustomHttpStatus.NOT_FOUND, "INVENTORY-001", "책 상세 정보를 찾지 못 했습니다.")
}