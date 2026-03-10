package plain.bookmaru.domain.affiliation.exception.errorCode

import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.error.ErrorCode

enum class AffiliationErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String

) : ErrorCode {
    NOT_FOUNT_AFFILIATION(CustomHttpStatus.NOT_FOUND, "AFFILIATION-001", "소속 정보를 찾지 못했습니다."),
    MATCH_AFFILIATION(CustomHttpStatus.CONFLICT, "AFFILIATION-002", "기존에 사용하던 소속 정보와 일치합니다.")
}