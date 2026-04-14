package plain.bookmaru.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.opensearch")
data class OpenSearchProperties(
    val port: String,
    val host: String,
    val username: String,
    val password: String
)