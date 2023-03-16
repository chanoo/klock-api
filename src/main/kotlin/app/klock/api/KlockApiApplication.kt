package app.klock.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KlockApiApplication

fun main(args: Array<String>) {
    runApplication<KlockApiApplication>(*args)
}
