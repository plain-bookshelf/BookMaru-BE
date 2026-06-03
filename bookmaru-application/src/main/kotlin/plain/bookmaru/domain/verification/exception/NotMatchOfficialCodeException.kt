package plain.bookmaru.domain.verification.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.verification.exception.errorcode.VerificationBaseErrorCode

class NotMatchOfficialCodeException(value: String) : BaseException(VerificationBaseErrorCode.NOT_MATCH_OFFICIAL_CODE, value) {
}