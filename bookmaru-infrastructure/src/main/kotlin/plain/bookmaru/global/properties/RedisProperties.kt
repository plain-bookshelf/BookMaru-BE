package plain.bookmaru.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.data.redis")
data class RedisProperties(
    val host: String,
    val port: Int,
    val password: String,
    val dbIndex: DbIndex
) {
    data class DbIndex(
        val auth: Int,
        val cache: Int
    )
}