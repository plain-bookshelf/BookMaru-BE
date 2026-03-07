package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.ProfileImageChangeUseCase
import plain.bookmaru.domain.member.port.`in`.command.ProfileImageChangeCommand
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class ProfileImageChangeService(
    private val memberPort: MemberPort
) : ProfileImageChangeUseCase{
    override suspend fun execute(command: ProfileImageChangeCommand) {
        val username = command.username
        val newProfileImageUrl = command.newProfileImageUrl

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 사용하는 유저 정보가 없습니다.")

        member.modifyProfileImage(newProfileImageUrl)
        memberPort.save(member)

        log.info { "$username 아이디를 가진 유저의 profileImage 정보를 $newProfileImageUrl (으)로 수정하는데 성공하였습니다." }
    }
}