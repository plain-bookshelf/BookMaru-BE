package plain.bookmaru.domain.community.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.community.exception.errorcode.CommunityErrorCode

class NoLikedException(value: String) : BaseException(CommunityErrorCode.NO_LIKED, value) {
}