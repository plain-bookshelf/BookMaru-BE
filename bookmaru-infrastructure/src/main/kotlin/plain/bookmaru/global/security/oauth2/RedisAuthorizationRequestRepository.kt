package plain.bookmaru.global.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.Duration
import java.util.Base64

@Component
class RedisAuthorizationRequestRepository(
    private val redisTemplate: StringRedisTemplate
) : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val state = request.getParameter("state") ?: return null
        val serializedData = redisTemplate.opsForValue().get("oauth2_req:$state") ?: return null
        return deserialize(serializedData)
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        if (authorizationRequest == null) return

        val state = authorizationRequest.state
        val serializedData = serialize(authorizationRequest)

        redisTemplate.opsForValue().set("oauth2_req:$state", serializedData, Duration.ofMinutes(3))
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): OAuth2AuthorizationRequest? {
        val state = request.getParameter("state") ?: return null
        val authRequest = loadAuthorizationRequest(request)
        redisTemplate.delete("oauth2_req:$state")
        return authRequest
    }

    private fun serialize(obj: OAuth2AuthorizationRequest): String {
        ByteArrayOutputStream().use { baos ->
            ObjectOutputStream(baos).use {
                it.writeObject(obj)
                return Base64.getUrlEncoder().encodeToString(baos.toByteArray())
            }
        }
    }

    private fun deserialize(data: String): OAuth2AuthorizationRequest? {
        return try {
            val decodeBytes = Base64.getUrlDecoder().decode(data)
            ByteArrayInputStream(decodeBytes).use {
                ObjectInputStream(it).use {
                    it.readObject() as OAuth2AuthorizationRequest
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}