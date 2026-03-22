package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberBaseErrorCode

class AlreadyExistsMemberException(value: String) : BaseException(MemberBaseErrorCode.ALREADY_EXISTS_MEMBER, value) {
}