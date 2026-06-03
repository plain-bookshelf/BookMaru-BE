package plain.bookmaru.domain.community.exception

import plain.bookmaru.common.error.BaseException
import plain.bookmaru.domain.community.exception.errorcode.CommunityErrorCode

class NotMatchWriterMemberException(value: String): BaseException(CommunityErrorCode.NOT_MATCH_WRITE_MEMBER, value)