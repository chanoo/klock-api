package app.klock.api.config

import app.klock.api.functional.FriendRelationRouter
import app.klock.api.functional.auth.AuthRouter
import app.klock.api.functional.chatBot.ChatBotRouter
import app.klock.api.functional.echo.EchoRouter
import app.klock.api.functional.studySession.StudySessionRouter
import app.klock.api.functional.tag.TagRouter
import app.klock.api.functional.timer.TimerExamRouter
import app.klock.api.functional.timer.TimerFocusRouter
import app.klock.api.functional.timer.TimerPomodoroRouter
import app.klock.api.functional.timer.TimerRouter
import app.klock.api.functional.user.UserRouter
import app.klock.api.functional.userTag.UserTagRouter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient

@TestConfiguration
class TestConfig(
  private val authRouter: AuthRouter,
  private val userRouter: UserRouter,
  private val timerRouter: TimerRouter,
  private val timerPomodoroRouter: TimerPomodoroRouter,
  private val timerExamRouter: TimerExamRouter,
  private val timerFocusRouter: TimerFocusRouter,
  private val tagRouter: TagRouter,
  private val echoRouter: EchoRouter,
  private val userTagRouter: UserTagRouter,
  private val studySessionRouter: StudySessionRouter,
  private val friendRelationRouter: FriendRelationRouter,
  private val chatBotRouter: ChatBotRouter
) {

  @Bean
  fun webTestClient(): WebTestClient {
    return WebTestClient
      .bindToRouterFunction(authRouter.authRoutes()
        .andOther(userRouter.userRoutes())
        .andOther(userTagRouter.userTagRoutes())
        .andOther(studySessionRouter.studySessionRoutes())
        .andOther(echoRouter.echoRoutes())
        .andOther(timerRouter.timerRoutes())
        .andOther(timerPomodoroRouter.timerRoutes())
        .andOther(timerExamRouter.timerRoutes())
        .andOther(timerFocusRouter.timerRoutes())
        .andOther(tagRouter.tagRoutes()) // TagRouter를 추가합니다.
        .andOther(chatBotRouter.chatBotRoutes()) // ChatBotRouter를 추가합니다.
        .andOther(friendRelationRouter.friendRelationRoutes())
      ) // FriendRelationRouter를 추가합니다.
      .configureClient()
      .build()
  }
}
