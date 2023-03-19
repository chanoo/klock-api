package app.klock.api.functional.tag

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class TagRouter(private val tagHandler: TagHandler) {

    @Bean
    fun tagRoutes() = router {
        "/api/tags".nest {
            GET("", tagHandler::findAll)
            GET("/{id}", tagHandler::findById)
            POST("", tagHandler::create)
            PUT("/{id}", tagHandler::update)
            DELETE("/{id}", tagHandler::deleteById)
        }
    }
}
