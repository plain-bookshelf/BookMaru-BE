package plain.bookmaru.domain.member.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.OftenReadBookTimeSetUseCase
import plain.bookmaru.domain.member.port.`in`.command.OftenReadBookTimeSetCommand
import plain.bookmaru.domain.member.port.out.MemberPort

@Service
class OftenReadBookTimeService(
    private val memberPort: MemberPort
) : OftenReadBookTimeSetUseCase{
    override suspend fun execute(command: OftenReadBookTimeSetCommand) {
        val time = command.time
        val username = command.username

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 가진 유저를 찾지 못 했습니다.")

        member.modifyOftenBookReadTime(time)

        memberPort.save(member)
    }
}