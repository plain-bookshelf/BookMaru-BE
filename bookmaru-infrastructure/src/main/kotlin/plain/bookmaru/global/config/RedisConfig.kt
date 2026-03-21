package plain.bookmaru.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import plain.bookmaru.global.properties.RedisProperties

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties
) {
    /*
    0 인증용(Auth)
     */
    @Bean
    @Primary
    fun authRedisConnectionFactory(): RedisConnectionFactory? {
        val config = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        config.database = redisProperties.dbIndex.auth

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
        val config = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        config.database = redisProperties.dbIndex.cache

        return LettuceConnectionFactory(config)
    }

    @Bean(name = ["cacheRedisTemplate"])
    fun cacheRedisTemplate(): RedisTemplate<String, ByteArray> {
        val template = RedisTemplate<String, ByteArray>()
        template.connectionFactory = cacheRedisConnectionFactory()

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = RedisSerializer.byteArray()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = RedisSerializer.byteArray()

        return template
    }
}