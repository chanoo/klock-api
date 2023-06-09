package app.klock.api.functional

import app.klock.api.functional.friendrelation.FriendRelationHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class FriendRelationRouter(private val friendRelationHandler: FriendRelationHandler) {

  @Bean
  fun friendRelationRoutes() = router {
    "/api/v1/friend-relations".nest {
      POST("", friendRelationHandler::create)
      GET("", friendRelationHandler::getFriendRelationsByRequesterId)
    }
  }

}
