package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberBaseErrorCode

class NotFoundMemberException(value: String) : BaseException(MemberBaseErrorCode.NOT_FOUND_MEMBER, value) {
}