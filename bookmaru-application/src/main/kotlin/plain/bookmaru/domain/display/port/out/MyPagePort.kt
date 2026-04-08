package plain.bookmaru.domain.display.port.out

import plain.bookmaru.domain.display.port.out.result.LendingBookListResult
import plain.bookmaru.domain.display.port.out.result.ViewMyPageResult

interface MyPagePort {
    suspend fun findUserInfoByUsername(username: String) : ViewMyPageResult
    suspend fun findUserLendingInfoByUsername(memberId: Long) : LendingBookListResult
}