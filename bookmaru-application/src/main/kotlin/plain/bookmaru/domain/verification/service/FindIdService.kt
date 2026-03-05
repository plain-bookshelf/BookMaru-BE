package plain.bookmaru.domain.verification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.ReadOnlyService
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.exception.NotMatchVerificationCodeException
import plain.bookmaru.domain.verification.port.`in`.FindIdUseCase
import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort
import plain.bookmaru.domain.verification.port.out.result.UsernameResult
import plain.bookmaru.domain.verification.vo.VerificationCodeType

private val log = KotlinLogging.logger {}

@ReadOnlyService
class FindIdService(
    private val emailVerificationCodePort: EmailVerificationCodePort,
    private val memberPort: MemberPort
) : FindIdUseCase {
    override suspend fun execute(command: VerificationCodeCommand): UsernameResult {
        val email = command.email
        val verificationCode = command.verificationCode

        val emailVerification = emailVerificationCodePort.load(command.email)

        if (emailVerification?.email == null)
            throw NotFoundEmailException("$email 이메일을 찾지 못 했습니다.")

        if (emailVerification.codeData.code != verificationCode ||
            emailVerification.codeData.codeType != VerificationCodeType.FIND_ID)
            throw NotMatchVerificationCodeException("$verificationCode 인증 코드 정보가 일치하지 않거나 다른 타입의 인증코드 입니다.")

        emailVerificationCodePort.delete(email)

        log.info { "$email 인증 완료" }

        val member = memberPort.findByEmail(Email(email))
            ?: throw NotFoundMemberException("$email 이메일을 사용하는 유저 정보가 없습니다.")

        return UsernameResult(member.accountInfo!!.username)
    }
}