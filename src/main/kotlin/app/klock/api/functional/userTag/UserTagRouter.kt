package app.klock.api.functional.userTag

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class UserTagRouter(private val userTagHandler: UserTagHandler) {

  @Bean
  fun userTagRoutes() = router {
    "/api/user-tags".nest {
      GET("", userTagHandler::getUserTags)
      POST("", userTagHandler::create)
    }
  }
}
