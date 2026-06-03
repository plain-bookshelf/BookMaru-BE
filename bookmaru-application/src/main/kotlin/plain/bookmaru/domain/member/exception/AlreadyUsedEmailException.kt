package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberErrorCode

class AlreadyUsedEmailException(value: String) : BaseException(MemberErrorCode.ALREADY_USED_EMAIL, value) {
}