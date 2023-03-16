package app.klock.api.websocket.handler

import org.springframework.web.reactive.socket.WebSocketHandler

interface WebSocketHandlerProvider : WebSocketHandler {
    fun getMapping(): String
}
