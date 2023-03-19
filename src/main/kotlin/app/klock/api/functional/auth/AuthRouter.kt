package app.klock.api.functional.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class AuthRouter(private val authHandler: AuthHandler) {

    @Bean
    fun authRoutes() = router {
        "/api/auth".nest {
            POST("/signup", authHandler::signup)
            POST("/signin", authHandler::signin)
            POST("/refresh-token", authHandler::refreshToken)
            POST("/facebook", authHandler::authenticateFacebook)
            POST("/apple", authHandler::authenticateApple)
        }
    }

}
