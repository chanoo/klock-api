package app.klock.api.websocket.config

import app.klock.api.websocket.handler.RealtimeUsersWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class SchedulerConfig(private val webSocketHandler: RealtimeUsersWebSocketHandler) {

    @Scheduled(fixedRate = 5000) // 접속자 수를 5초마다 전송
    fun sendConnectedUsersCount() {
        webSocketHandler.broadcastConnectedUsers()
    }
}
