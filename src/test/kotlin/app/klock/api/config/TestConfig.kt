package app.klock.api.config

import app.klock.api.functional.FriendRelationRouter
import app.klock.api.functional.account.AccountRouter
import app.klock.api.functional.accountTag.AccountTagRouter
import app.klock.api.functional.auth.AuthRouter
import app.klock.api.functional.chatbot.ChatBotRouter
import app.klock.api.functional.echo.EchoRouter
import app.klock.api.functional.studySession.StudySessionRouter
import app.klock.api.functional.tag.TagRouter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient

@TestConfiguration
class TestConfig(
  private val authRouter: AuthRouter,
  private val accountRouter: AccountRouter,
  private val tagRouter: TagRouter,
  private val echoRouter: EchoRouter,
  private val accountTagRouter: AccountTagRouter,
  private val studySessionRouter: StudySessionRouter,
  private val friendRelationRouter: FriendRelationRouter,
  private val chatBotRouter: ChatBotRouter
) {

  @Bean
  fun webTestClient(): WebTestClient {
    return WebTestClient
      .bindToRouterFunction(authRouter.authRoutes()
        .andOther(accountRouter.userRoutes())
        .andOther(accountTagRouter.accountTagRoutes())
        .andOther(studySessionRouter.studySessionRoutes())
        .andOther(echoRouter.echoRoutes())
        .andOther(tagRouter.tagRoutes()) // TagRouter를 추가합니다.
        .andOther(chatBotRouter.chatBotRoutes()) // ChatBotRouter를 추가합니다.
        .andOther(friendRelationRouter.friendRelationRoutes())
      ) // FriendRelationRouter를 추가합니다.
      .configureClient()
      .build()
  }
}
