package plain.bookmaru.domain.community.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.community.exception.errorcode.CommunityBaseErrorCode

class NotFoundCommentException(value: String): BaseException(CommunityBaseErrorCode.NOT_FOUND_COMMENT, value) {
}