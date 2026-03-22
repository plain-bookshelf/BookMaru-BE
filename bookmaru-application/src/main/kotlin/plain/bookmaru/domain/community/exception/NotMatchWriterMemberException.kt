package plain.bookmaru.domain.community.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.community.exception.errorcode.CommunityBaseErrorCode

class NotMatchWriterMemberException(value: String): BaseException(CommunityBaseErrorCode.NOT_MATCH_WRITE_MEMBER, value)