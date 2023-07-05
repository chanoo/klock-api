package app.klock.api.functional.friemdRelation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class FriendRelationRouter(private val friendRelationHandler: FriendRelationHandler) {

  @Bean
  fun friendRelationRoutes() = router {
    "/api/friend-relations".nest {
      POST("/follow", friendRelationHandler::follow)
      POST("/unfollow", friendRelationHandler::unfollow)
      GET("", friendRelationHandler::getFriendRelations)
    }
  }

}
