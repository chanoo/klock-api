package app.klock.api.functional.account

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class AccountRouter(private val accountHandler: AccountHandler) {
    @Bean
    fun userRoutes() = router {
        "/api/users".nest {
            GET("", accountHandler::getAllUsers)
            GET("/{id}", accountHandler::getUserById)
            PUT("/{id}", accountHandler::updateUser)
            DELETE("/{id}", accountHandler::deleteUser)
        }
    }
}
