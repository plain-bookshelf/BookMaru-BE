package plain.bookmaru.domain.verification.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.verification.exception.errorcode.VerificationErrorCode

class NotMatchEmailMemberException(value: String) : BaseException(VerificationErrorCode.NOT_MATCH_EMAIL_MEMBER, value) {
}