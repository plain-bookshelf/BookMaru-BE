package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberErrorCode

class AlreadyExistsMemberException(value: String) : BaseException(MemberErrorCode.ALREADY_EXISTS_MEMBER, value) {
}