package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.`in`.ViewRankingPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewRankingPageCommand
import plain.bookmaru.domain.display.port.out.RankingPagePort
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult
import plain.bookmaru.domain.member.port.out.MemberProfileImageStoragePort

private val log = KotlinLogging.logger {}

@Service
class ViewRankingPageService(
    private val rankingPagePort: RankingPagePort,
    private val memberProfileImageStoragePort: MemberProfileImageStoragePort
): ViewRankingPageUseCase {
    override suspend fun execute(command: ViewRankingPageCommand): List<UserRankInfoResult> {
        val cacheResult = rankingPagePort.loadRankingPage(command.affiliationId)

        if (cacheResult != null) return cacheResult.withPublicProfileImages()

        log.warn { "Ranking 캐시 miss 로 인해 빈 리스트를 반환합니다. (AffiliationId: ${command.affiliationId})" }

        return emptyList()
    }

    private fun List<UserRankInfoResult>.withPublicProfileImages(): List<UserRankInfoResult> {
        return map {
            it.copy(profileImage = memberProfileImageStoragePort.toPublicUrl(it.profileImage))
        }
    }
}
