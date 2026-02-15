package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberErrorCode

class MemberNotFoundException(value: String) : BaseException(MemberErrorCode.NOT_FOUND_MEMBER, value) {
}