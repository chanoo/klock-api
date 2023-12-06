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
        val userId = session.attributes["userId"] as? String ?: "unknown"
        handleUserConnection(userId, true) // 사용자 연결 처리

        return session.receive()
            .doFinally {
                sessions.remove(session)
                val userId = session.attributes["user_id"] as? String
                if (userId != null) {
                    handleUserConnection(userId, false) // 사용자 연결 해제 처리
                }
            }
            .then()
    }

    fun handleUserConnection(userId: String, isConnected: Boolean) {
        if (isConnected) {

        } else {

        }
    }

    fun broadcastConnectedUsers() {
        val message = "{\"connected_users\": ${sessions.size}}"
        sessions.forEach { session ->
            session.send(Mono.just(session.textMessage(message))).subscribe()
        }
    }

    fun broadcastOnlineUsers() {
        val onlineUserIds = sessions.mapNotNull { it.attributes["user_id"] as? String }
        val message = "{\"online_users\": ${onlineUserIds.joinToString()}, \"total_users\": ${sessions.size}}"
        sessions.forEach { session ->
            session.send(Mono.just(session.textMessage(message))).subscribe()
        }
    }
}
