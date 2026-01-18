package plain.bookmaru

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookmaruInfrastructureApplication

fun main(args: Array<String>) {
    runApplication<BookmaruInfrastructureApplication>(*args)
}