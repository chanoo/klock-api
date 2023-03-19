package app.klock.api.functional.accountTag

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class AccountTagRouter(private val accountTagHandler: AccountTagHandler) {

    @Bean
    fun accountTagRoutes() = router {
        "/api/account-tags".nest {
            GET("", accountTagHandler::getAccountTags)
            POST("", accountTagHandler::createAccountTag)
        }
    }
}
