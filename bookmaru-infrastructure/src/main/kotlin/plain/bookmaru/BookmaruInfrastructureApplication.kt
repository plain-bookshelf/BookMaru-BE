package plain.bookmaru

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication()
@EnableJpaAuditing
@ConfigurationPropertiesScan
@EnableRedisRepositories
@EnableScheduling
class BookmaruInfrastructureApplication

    fun main(args: Array<String>) {
        runApplication<BookmaruInfrastructureApplication>(*args)
    }