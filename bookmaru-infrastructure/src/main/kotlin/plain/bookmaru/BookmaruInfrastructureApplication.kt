package plain.bookmaru

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class BookmaruInfrastructureApplication

    fun main(args: Array<String>) {
        runApplication<BookmaruInfrastructureApplication>(*args)
    }