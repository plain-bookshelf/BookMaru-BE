package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.service.BlackListProfessor
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.DeleteMemberUseCase
import plain.bookmaru.domain.member.port.`in`.command.DeleteMemberCommand
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class DeleteMemberService(
    private val memberPort: MemberPort,
    private val refreshTokenPort: RefreshTokenPort,
    private val blackListProfessor: BlackListProfessor
) : DeleteMemberUseCase {
    override suspend fun deleteMember(command: DeleteMemberCommand) {
        val username = command.username
        val accessToken = resolveToken(command.accessToken)

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 사용하는 유저 정보를 찾지 못 했습니다.")

        memberPort.delete(member)
        log.info { "$username 아이디를 사용하는 유저 정보를 지우는데 성공했습니다." }

        blackListProfessor.execute(accessToken)
        refreshTokenPort.deleteByUsername(username)
        log.info { "$username 을 사용하는 refreshToken 정보를 지우는데 성공했습니다." }
    }

    private fun resolveToken(token: String): String = token.substringAfter("Bearer ").trim()
}