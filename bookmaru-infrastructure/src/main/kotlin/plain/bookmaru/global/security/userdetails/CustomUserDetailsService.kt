package plain.bookmaru.global.security.userdetails

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.domain.member.vo.Email

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val member = if (username.contains("@")) memberRepository.findByEmail(username)
        else memberRepository.findByUsername(username)
            ?: throw NotFoundMemberException("유저 정보를 찾지 못 했습니다: $username")

        return CustomUserDetails(member!!)
    }
}