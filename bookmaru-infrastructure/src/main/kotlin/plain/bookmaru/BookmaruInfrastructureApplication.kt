package plain.bookmaru

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
@EnableRedisRepositories
@EnableElasticsearchRepositories(basePackages = ["plain"])
class BookmaruInfrastructureApplication

    fun main(args: Array<String>) {
        runApplication<BookmaruInfrastructureApplication>(*args)
    }