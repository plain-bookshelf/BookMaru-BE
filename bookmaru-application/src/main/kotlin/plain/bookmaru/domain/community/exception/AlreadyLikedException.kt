package plain.bookmaru.domain.community.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.community.exception.errorcode.CommunityErrorCode

class AlreadyLikedException(value: String) : BaseException(CommunityErrorCode.ALREADY_LIKED, value) {
}