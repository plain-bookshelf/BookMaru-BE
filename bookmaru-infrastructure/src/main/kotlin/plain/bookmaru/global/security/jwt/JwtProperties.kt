package plain.bookmaru.global.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val header: String,
    val prefix: String,
    val accessExp: Duration,
    val refreshExp: Duration
)