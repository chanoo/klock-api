package app.klock.api.websocket.handler

import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Component
class RealtimeUsersWebSocketHandler : WebSocketHandlerProvider {

    private val sessions = ConcurrentHashMap.newKeySet<WebSocketSession>()

    override fun getMapping(): String {
        return "/ws/realtime-users"
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        sessions.add(session)

        return session.receive()
            .doFinally { sessions.remove(session) }
            .then()
    }

    fun broadcastConnectedUsers() {
        val message = "{\"connected_users\": ${sessions.size}}"
        sessions.forEach { session ->
            session.send(Mono.just(session.textMessage(message))).subscribe()
        }
    }
}
