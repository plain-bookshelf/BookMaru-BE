package plain.bookmaru.global.security.userdetails

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import java.util.Collections

data class CustomUserDetails(
    val id: Long,
    private val username: String,
    private val password: String,
    private val role: Authority,
    private val affiliationId: Long,
    private val isEnabled: Boolean = true,
) : UserDetails {

    constructor(memberEntity: MemberEntity) : this(
        id = memberEntity.id!!,
        username = memberEntity.username,
        password = memberEntity.password ?: "",
        role = memberEntity.role,
        affiliationId = memberEntity.affiliation!!.id!!,
        isEnabled = memberEntity.affiliation.affiliationName.isNotBlank()
    )

    override fun getUsername(): String? = username

    override fun getPassword(): String? = password

    override fun getAuthorities(): Collection<GrantedAuthority?>?
    = Collections.singleton(SimpleGrantedAuthority(role.name))

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = role != Authority.ROLE_BANNED

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = isEnabled
}