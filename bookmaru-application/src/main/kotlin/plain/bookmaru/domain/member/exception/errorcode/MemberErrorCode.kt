package plain.bookmaru.domain.member.exception.errorcode

import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.error.ErrorCode

enum class MemberErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
) : ErrorCode {
    ALREADY_EXISTS_MEMBER(CustomHttpStatus.CONFLICT, "MEMBER-001", "이미 존재하는 유저입니다."),
    ALREADY_USED_EMAIL(CustomHttpStatus.CONFLICT, "MEMBER-002", "이미 사용되는 이메일입니다."),
    NOT_FOUND_MEMBER(CustomHttpStatus.NOT_FOUND, "AUTH-003", "유저를 찾지 못 했습니다.")
}