package plain.bookmaru.domain.event.exception.errorcode

import plain.bookmaru.common.error.BaseErrorCode
import plain.bookmaru.common.error.CustomHttpStatus

enum class EventErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
): BaseErrorCode {
    NOT_CONTAIN_CONTENT(CustomHttpStatus.BAD_REQUEST, "EVENT-001", "이벤트 상세 내용 정보가 없습니다."),
    NOT_FOUND_EVENT(CustomHttpStatus.NOT_FOUND, "EVENT-002", "이벤트 정보를 찾지 못 했습니다."),
    NOT_EVENT_CREATE_USER(CustomHttpStatus.FORBIDDEN, "EVENT_003", "이벤트를 만들지 않은 유저가 이벤트를 지우려고 시도 했습니다.")
}