package plain.bookmaru.domain.member.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.member.exception.errorcode.MemberBaseErrorCode

class NotMatchExistingPasswordException(value: String) : BaseException(MemberBaseErrorCode.NOT_MATCH_EXISTING_PASSWORD, value)