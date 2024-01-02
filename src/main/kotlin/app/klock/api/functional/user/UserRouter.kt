package app.klock.api.functional.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class UserRouter(private val userHandler: UserHandler) {
  @Bean
  fun userRoutes() = router {
    "/api/v1/users".nest {
      GET("", userHandler::getAllUsers)
      GET("/{id}", userHandler::getUserById)
      PUT("/{id}", userHandler::updateUser)
      DELETE("/{id}", userHandler::deleteUser)
      POST("/existed-nickname", userHandler::existedNickname)
      POST("/{id}/profile-image", userHandler::updateProfileImage)
      POST("/search-by-nickname", userHandler::searchByNickname)
    }
  }
}
