package plain.bookmaru.domain.verification.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.verification.exception.errorcode.VerificationErrorCode

class NotMatchOfficialCodeException(value: String) : BaseException(VerificationErrorCode.NOT_MATCH_OFFICIAL_CODE, value) {
}