package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.NicknameChangeUseCase
import plain.bookmaru.domain.member.port.`in`.command.NicknameChangeCommand
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class NicknameChangeService(
    private val memberPort: MemberPort
) : NicknameChangeUseCase {
    override suspend fun execute(command: NicknameChangeCommand) {
        val newNickname = command.newNickname
        val username = command.username

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 사용하는 유저 정보가 없습니다.")

        member.modifyNickname(newNickname)
        memberPort.save(member)

        log.info { "$username 아이디를 사용하는 유저의 닉네임 정보를 $newNickname (으)로 변경하는데 성공했습니다." }
    }
}