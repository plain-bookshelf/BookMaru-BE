package plain.bookmaru.domain.community.exception.errorcode

import plain.bookmaru.common.error.BaseErrorCode
import plain.bookmaru.common.error.CustomHttpStatus

enum class CommunityBaseErrorCode(
    override val status: CustomHttpStatus,
    override val code: String,
    override val message: String
): BaseErrorCode {
    NOT_FOUND_COMMENT(CustomHttpStatus.NOT_FOUND, "COMMUNITY-001", "댓글을 정보를 찾지 못 했습니다."),
    NOT_MATCH_WRITE_MEMBER(CustomHttpStatus.NOT_FOUND, "COMMUNITY-002", "댓글 작성자와 다른 유저가 댓글을 변경하려고 시도 했습니다.")
}