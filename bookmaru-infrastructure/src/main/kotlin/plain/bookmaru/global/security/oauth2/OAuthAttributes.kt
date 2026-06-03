package plain.bookmaru.global.security.oauth2

import plain.bookmaru.domain.auth.exception.UnsupportedOAuth2Exception
import plain.bookmaru.domain.auth.vo.OAuthInfo
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.member.vo.Email

data class OAuthAttributes(
    val oAuthInfo: OAuthInfo,
    val email: Email,
    val nickname: String,
    val profileImageUrl: String
) {
    companion object {
        fun of(registrationId: String, attributes: Map<String, Any>) : OAuthAttributes {
            return when (registrationId.uppercase()) {
                "KAKAO" -> ofKakao(attributes)
                "GOOGLE" -> ofGoogle(attributes)
                "NAVER" -> ofNaver(attributes)
                else -> throw UnsupportedOAuth2Exception("$attributes 지원하지 않는 정보가 소셜 로그인 정보가 들어왔습니다.")
            }
        }

        private fun ofKakao(attributes: Map<String, Any>) : OAuthAttributes {
            val kakaoAccount = attributes["kakao_account"] as Map<*, *>
            val profile = kakaoAccount["profile"] as Map<*, *>

            return OAuthAttributes(
                oAuthInfo = OAuthInfo(OAuthProvider.KAKAO, attributes["id"].toString()),
                email = Email(kakaoAccount["email"] as? String ?: ""),
                nickname = profile["nickname"] as? String ?: "익명",
                profileImageUrl = profile["profile_image_url"] as? String ?: ""
            )
        }

        private fun ofGoogle(attributes: Map<String, Any>) : OAuthAttributes {
            return OAuthAttributes(
                oAuthInfo = OAuthInfo(OAuthProvider.GOOGLE, attributes["sub"].toString()),
                email = Email(attributes["email"] as String),
                nickname = attributes["name"] as String,
                profileImageUrl = attributes["picture"] as String
            )
        }

        private fun ofNaver(attributes: Map<String, Any>) : OAuthAttributes {
            val response = attributes["response"] as Map<*, *>

            return OAuthAttributes(
                oAuthInfo = OAuthInfo(OAuthProvider.NAVER, response["id"].toString()),
                email = Email(response["email"] as? String ?: ""),
                nickname = response["name"] as? String ?: "익명",
                profileImageUrl = response["profile_image"] as? String ?: ""
            )
        }
    }
}