package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.service.BlackListProfessor
import plain.bookmaru.domain.display.scope.CacheCoroutineScope
import plain.bookmaru.domain.display.service.cache.RankingPageCacheService
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.exception.RentalOrReservationBookExistException
import plain.bookmaru.domain.member.port.`in`.DeleteMemberUseCase
import plain.bookmaru.domain.member.port.`in`.command.DeleteMemberCommand
import plain.bookmaru.domain.member.port.out.MemberDevicePort
import plain.bookmaru.domain.member.port.out.MemberPort
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class DeleteMemberService(
    private val memberPort: MemberPort,
    private val memberDevicePort: MemberDevicePort,
    private val refreshTokenPort: RefreshTokenPort,
    private val rankingPageCacheService: RankingPageCacheService,
    private val cacheCoroutineScope: CacheCoroutineScope,
    private val blackListProfessor: BlackListProfessor
) : DeleteMemberUseCase {

    override suspend fun deleteMember(command: DeleteMemberCommand) {
        val username = command.username
        val accessToken = resolveToken(command.accessToken)

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디의 유저를 찾지 못 했습니다.")

        if (member.lendingBook.rentalCount != 0 || member.lendingBook.reservationCount != 0)
            throw RentalOrReservationBookExistException()

        val uuid = UUID.randomUUID().toString()
        val suffix = uuid.take(8)
        member.deleteStatus()
        member.modifyNickname("delete_user:${member.profile.nickname}:$suffix")
        member.modifyEmail("deleted$suffix@bookmaru.invalid")
        member.modifyUsername("delete_user:${member.accountInfo?.username}:$suffix")

        memberPort.delete(member)
        log.info { "$username 회원 소프트 삭제를 완료했습니다." }

        blackListProfessor.execute(accessToken)
        memberDevicePort.deleteAllByMemberId(member.id!!)
        refreshTokenPort.deleteByUsername(username)

        cacheCoroutineScope.launch {
            runCatching {
                rankingPageCacheService.upRanking(member.affiliationId!!)
            }.onFailure {
                log.warn(it) { "$username 회원 삭제 후 랭킹 캐시 갱신에 실패했습니다." }
            }
        }

        log.info { "$username 회원의 세션 무효화를 완료했습니다." }
    }

    private fun resolveToken(token: String): String = token.substringAfter("Bearer ").trim()
}
