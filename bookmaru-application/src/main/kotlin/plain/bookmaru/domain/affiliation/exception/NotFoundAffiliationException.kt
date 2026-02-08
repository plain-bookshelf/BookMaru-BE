package plain.bookmaru.domain.affiliation.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.affiliation.exception.errorCode.AffiliationErrorCode

class NotFoundAffiliationException(value: String) : BaseException(AffiliationErrorCode.NOT_FOUNT_AFFILIATION, value)