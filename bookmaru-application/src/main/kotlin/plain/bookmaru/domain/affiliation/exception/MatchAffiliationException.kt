package plain.bookmaru.domain.affiliation.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.affiliation.exception.errorCode.AffiliationBaseErrorCode

class MatchAffiliationException(value: String) : BaseException(AffiliationBaseErrorCode.MATCH_AFFILIATION, value) {
}