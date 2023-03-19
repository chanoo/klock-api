package app.klock.api.config

import app.klock.api.functional.auth.router.AuthRouter
import app.klock.api.functional.auth.router.UserRouter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient

@TestConfiguration
class TestConfig(
    private val authRouter: AuthRouter,
    private val userRouter: UserRouter
) {

    @Bean
    fun webTestClient(): WebTestClient {
        return WebTestClient
            .bindToRouterFunction(authRouter.authRoutes()
            .andOther(userRouter.userRoutes()))
            .configureClient()
            .build()
    }
}
