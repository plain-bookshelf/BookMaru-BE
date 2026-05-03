package plain.bookmaru.domain.member.exception.errorcode

import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.error.BaseErrorCode

enum class MemberErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    ALREADY_EXISTS_MEMBER(CustomHttpStatus.CONFLICT, "MEMBER-001", "이미 존재하는 유저입니다."),
    ALREADY_USED_EMAIL(CustomHttpStatus.CONFLICT, "MEMBER-002", "이미 사용되는 이메일입니다."),
    NOT_FOUND_MEMBER(CustomHttpStatus.NOT_FOUND, "MEMBER-003", "유저를 찾지 못 했습니다."),
    USED_PASSWORD(CustomHttpStatus.BAD_REQUEST, "MEMBER-004", "기존에 사용하던 비밀번호 입니다. 다른 값을 넣어주세요."),
    NOT_MATCH_EXISTING_PASSWORD(CustomHttpStatus.BAD_REQUEST, "MEMBER-005", "기존 비밀번호 정보가 일치하지 않습니다."),
    ALREADY_USED_NICKNAME(CustomHttpStatus.CONFLICT, "MEMBER-006", "이미 사용중인 닉네임입니다."),
    RENTAL_OR_RESERVATION_BOOK_EXIST(CustomHttpStatus.BAD_REQUEST, "MEMBER-007", "대여하거나 예약한 책이 존재합니다.")
}