package app.klock.api.config

import app.klock.api.functional.auth.router.AuthRouter
import app.klock.api.functional.auth.router.UserRouter
import app.klock.api.functional.tag.router.TagRouter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient

@TestConfiguration
class TestConfig(
    private val authRouter: AuthRouter,
    private val userRouter: UserRouter,
    private val tagRouter: TagRouter
) {

    @Bean
    fun webTestClient(): WebTestClient {
        return WebTestClient
            .bindToRouterFunction(authRouter.authRoutes()
            .andOther(userRouter.userRoutes())
            .andOther(tagRouter.tagRoutes())) // TagRouter를 추가합니다.
            .configureClient()
            .build()
    }
}
