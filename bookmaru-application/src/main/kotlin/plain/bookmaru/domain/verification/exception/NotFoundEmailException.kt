package plain.bookmaru.domain.verification.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.verification.exception.errorcode.VerificationErrorCode

class NotFoundEmailException(value: String) : BaseException(VerificationErrorCode.NOT_FOUND_EMAIL, value)