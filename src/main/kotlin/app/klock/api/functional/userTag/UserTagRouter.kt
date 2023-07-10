package app.klock.api.functional.userTag

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class UserTagRouter(private val userTagHandler: UserTagHandler) {

  @Bean
  fun userTagRoutes() = router {
    "/api/v1/user-tag".nest {
      GET("", userTagHandler::getUserTag)
      POST("", userTagHandler::create)
    }
  }
}
