package app.klock.api.functional.deploy

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class DeployRouter(private val deployHandler: DeployHandler) {

    @Bean
    fun deployRoutes() = router {
        "/api/deploy".nest {
            GET("/ready", deployHandler::ready)
            GET("/healthy", deployHandler::healthy)
        }
    }

}
