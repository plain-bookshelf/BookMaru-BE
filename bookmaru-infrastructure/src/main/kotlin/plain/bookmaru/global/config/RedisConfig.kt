package plain.bookmaru.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int,
    @Value("\${spring.data.redis.db-index.auth}") private val authIndex: Int,
    @Value("\${spring.data.redis.db-index.cache}") private val cacheIndex: Int
) {
    /*
    0 인증용(Auth)
     */
    @Bean
    @Primary
    fun authRedisConnectionFactory(): RedisConnectionFactory? {
        val config = RedisStandaloneConfiguration(host, port)
        config.database = authIndex

        return LettuceConnectionFactory(config)
    }

    @Bean
    @Primary
    fun authRedisTemplate(): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()

        template.connectionFactory = authRedisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()

        return template
    }

    /*
    1 캐시용(cache)
     */
    @Bean(name = ["cacheRedisConnectionFactory"])
    fun cacheRedisConnectionFactory(): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port)
        config.database = cacheIndex

        return LettuceConnectionFactory(config)
    }

    @Bean(name = ["cacheRedisTemplate"])
    fun cacheRedisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()

        template.connectionFactory = cacheRedisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()

        return template
    }
}