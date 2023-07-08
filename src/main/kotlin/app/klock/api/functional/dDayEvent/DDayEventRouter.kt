package app.klock.api.functional.dDayEvent

import app.klock.api.functional.handler.DDayEventHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class DDayEventRouter(private val handler: DDayEventHandler) {

    @Bean
    fun dDayEventRoutes(): RouterFunction<ServerResponse> = coRouter {
        "/api/v1/d-day".nest {
            GET("", handler::getById)
            GET("/{id}", handler::getById)
            POST("/", handler::create)
            PUT("/{id}", handler::update)
            DELETE("/{id}", handler::delete)
        }
    }
}
