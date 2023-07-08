package app.klock.api.functional.tag

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class TagRouter(private val tagHandler: TagHandler) {

  @Bean
  fun tagRoutes() = router {
    "/api/v1/tags".nest {
      GET("", tagHandler::getall)
      GET("/{id}", tagHandler::get)
      POST("", tagHandler::create)
      PUT("/{id}", tagHandler::update)
      DELETE("/{id}", tagHandler::delete)
    }
  }
}
