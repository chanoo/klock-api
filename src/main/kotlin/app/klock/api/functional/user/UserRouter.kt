package app.klock.api.functional.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class UserRouter(private val userHandler: UserHandler) {
  @Bean
  fun userRoutes() = router {
    "/api/users".nest {
      GET("", userHandler::getAllUsers)
      GET("/{id}", userHandler::getUserById)
      PUT("/{id}", userHandler::updateUser)
      DELETE("/{id}", userHandler::deleteUser)
      POST("/existed-nickname", userHandler::existedNickname)
    }
  }
}
