package app.klock.api.functional.auth.router

import app.klock.api.functional.auth.handler.AuthHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class AuthRouter(private val authHandler: AuthHandler) {

    @Bean
    fun authRoutes() = router {
        "/api/auth".nest {
            POST("/signup", authHandler::signup)
            POST("/login", authHandler::login)
            POST("/facebook", authHandler::authenticateFacebook)
            POST("/apple", authHandler::authenticateApple)
        }
    }
}
