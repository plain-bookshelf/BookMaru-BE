package plain.bookmaru.domain.verification.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.verification.exception.errorcode.VerificationBaseErrorCode

class NotFoundEmailException(value: String) : BaseException(VerificationBaseErrorCode.NOT_FOUND_EMAIL, value)