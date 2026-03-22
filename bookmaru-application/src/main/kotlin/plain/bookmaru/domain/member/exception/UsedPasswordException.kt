package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberBaseErrorCode

class UsedPasswordException(value: String) : BaseException(MemberBaseErrorCode.USED_PASSWORD, value) {
}