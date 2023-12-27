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
      PUT("/{trace_id}/heart", userTraceHandler::updateHeart)
      POST("/study", userTraceHandler::createStudyContent)
    }
  }
}
