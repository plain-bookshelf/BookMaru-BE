package plain.bookmaru.domain.event.exception.errorcode

import plain.bookmaru.common.error.BaseErrorCode
import plain.bookmaru.common.error.CustomHttpStatus

enum class EventErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
): BaseErrorCode {
    NOT_CONTAIN_CONTENT(CustomHttpStatus.BAD_REQUEST, "EVENT-001", "이벤트 상세 내용 정보가 없습니다."),
}