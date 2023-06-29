package app.klock.api.functional.friemdRelation

import app.klock.api.functional.friendrelation.FriendRelationHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class FriendRelationRouter(private val friendRelationHandler: FriendRelationHandler) {

  @Bean
  fun friendRelationRoutes() = router {
    "/api/friend-relations".nest {
      POST("", friendRelationHandler::create)
      GET("", friendRelationHandler::getFriendRelations)
      DELETE("/{id}", friendRelationHandler::delete)
    }
  }

}
