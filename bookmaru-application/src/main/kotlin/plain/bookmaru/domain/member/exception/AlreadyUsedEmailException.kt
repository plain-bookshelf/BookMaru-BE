package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberBaseErrorCode

class AlreadyUsedEmailException(value: String) : BaseException(MemberBaseErrorCode.ALREADY_USED_EMAIL, value) {
}