package plain.bookmaru.domain.community.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.community.exception.errorcode.CommunityErrorCode

class NotFoundCommentException(value: String): BaseException(CommunityErrorCode.NOT_FOUND_COMMENT, value) {
}