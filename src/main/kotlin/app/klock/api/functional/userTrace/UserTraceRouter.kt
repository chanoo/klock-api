package app.klock.api.functional.userTrace

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class UserTraceRouter(private val userTraceHandler: UserTraceHandler) {

  @Bean
  fun userTraceRoutes() = router {
    "/api/v1/user-trace".nest {
      GET("", userTraceHandler::getUserTrace)
      POST("", userTraceHandler::createContentWithImage)
//      POST("/image/{write_user_id}", userTraceHandler::createImage)
      PUT("/{trace_id}/heart", userTraceHandler::updateHeart)
      DELETE("/{trace_id}/heart", userTraceHandler::cancelHeart)
      DELETE("/{trace_id}", userTraceHandler::deleteUserTrace)
    }
  }
}
