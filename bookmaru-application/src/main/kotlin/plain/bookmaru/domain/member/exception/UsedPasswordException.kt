package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberErrorCode

class UsedPasswordException(value: String) : BaseException(MemberErrorCode.USED_PASSWORD, value) {
}