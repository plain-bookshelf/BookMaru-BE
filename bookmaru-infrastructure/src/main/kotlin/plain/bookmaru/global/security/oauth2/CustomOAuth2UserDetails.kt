package plain.bookmaru.global.security.oauth2

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2UserDetails(
    val attributes: OAuthAttributes
) : OAuth2User{
    override fun getAttributes(): Map<String?, Any?> {
        return mapOf(
            "oAuthInfo" to attributes.oAuthInfo,
            "email" to attributes.email,
        )
    }

    override fun getAuthorities(): Collection<GrantedAuthority?>? = mutableListOf()

    override fun getName(): String? = attributes.oAuthInfo.providerId
}