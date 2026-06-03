package plain.bookmaru.domain.member.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.OftenReadBookTimeSetUseCase
import plain.bookmaru.domain.member.port.`in`.command.OftenReadBookTimeSetCommand
import plain.bookmaru.domain.member.port.out.MemberPort

@Service
class OftenReadBookTimeService(
    private val memberPort: MemberPort,
    private val transactionPort: TransactionPort
) : OftenReadBookTimeSetUseCase{
    override suspend fun execute(command: OftenReadBookTimeSetCommand) {
        val time = command.time
        val username = command.username

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 가진 유저를 찾지 못 했습니다.")

        member.modifyOftenBookReadTime(time)

        transactionPort.withTransaction {
            memberPort.save(member)
        }
    }
}