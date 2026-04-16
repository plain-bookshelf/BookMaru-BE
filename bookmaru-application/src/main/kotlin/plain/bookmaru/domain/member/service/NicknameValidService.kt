package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.member.port.`in`.NicknameValidUseCase
import plain.bookmaru.domain.member.port.`in`.command.NicknameValidCommand
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class NicknameValidService(
    private val memberPort: MemberPort
): NicknameValidUseCase {
    override suspend fun execute(command: NicknameValidCommand): Boolean {
        log.info { "닉네임 검증 로직 시도" }
        return memberPort.validateNickname(command.newNickname)
    }
}