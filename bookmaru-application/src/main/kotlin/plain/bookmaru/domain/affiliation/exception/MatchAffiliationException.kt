package plain.bookmaru.domain.affiliation.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.affiliation.exception.errorCode.AffiliationErrorCode

class MatchAffiliationException(value: String) : BaseException(AffiliationErrorCode.MATCH_AFFILIATION, value) {
}