package app.klock.api.config

import app.klock.api.functional.account.AccountRouter
import app.klock.api.functional.accountTag.AccountTagRouter
import app.klock.api.functional.auth.AuthRouter
import app.klock.api.functional.tag.TagRouter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient

@TestConfiguration
class TestConfig(
    private val authRouter: AuthRouter,
    private val accountRouter: AccountRouter,
    private val tagRouter: TagRouter,
    private val accountTagRouter: AccountTagRouter
) {

    @Bean
    fun webTestClient(): WebTestClient {
        return WebTestClient
            .bindToRouterFunction(authRouter.authRoutes()
                .andOther(accountRouter.userRoutes())
                .andOther(accountTagRouter.accountTagRoutes())
                .andOther(tagRouter.tagRoutes())) // TagRouter를 추가합니다.
            .configureClient()
            .build()
    }
}
