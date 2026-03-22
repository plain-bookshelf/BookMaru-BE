package plain.bookmaru.domain.affiliation.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.affiliation.exception.errorCode.AffiliationBaseErrorCode

class NotFoundAffiliationException(value: String) : BaseException(AffiliationBaseErrorCode.NOT_FOUNT_AFFILIATION, value)