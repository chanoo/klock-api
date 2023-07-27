package app.klock.api.functional.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class AuthRouter(private val authHandler: AuthHandler) {
  @Bean
  fun authRoutes() = router {
    "/api/v1/auth".nest {
      POST("/signup", authHandler::signup)
      POST("/signin", authHandler::signin)
      POST("/refresh-token", authHandler::refreshToken)
      POST("/signin-with-apple", authHandler::authenticateApple)
      POST("/social-login", authHandler::authenticateSocial)
    }
  }
}
