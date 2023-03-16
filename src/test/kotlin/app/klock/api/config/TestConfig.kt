package app.klock.api.config

import app.klock.api.functional.user.handler.UserHandler
import app.klock.api.functional.user.router.UserRouter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient

@TestConfiguration
class TestConfig(private val userRouter: UserRouter) {

    @Bean
    fun webTestClient(): WebTestClient {
        return WebTestClient.bindToRouterFunction(userRouter.userRoutes())
            .configureClient()
            .baseUrl("/api")
            .build()
    }
}
