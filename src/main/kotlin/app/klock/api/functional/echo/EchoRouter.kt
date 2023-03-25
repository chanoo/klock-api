package app.klock.api.functional.echo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class EchoRouter(private val echoHandler: EchoHandler) {

    @Bean
    fun echoRoutes() = router {
        "/echo".nest {
            GET("", echoHandler::echo)
        }
    }
}
