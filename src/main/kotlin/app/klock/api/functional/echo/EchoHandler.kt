package app.klock.api.functional.echo

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.json
import reactor.core.publisher.Mono

@Component
class EchoHandler {

    fun get(request: ServerRequest): Mono<ServerResponse> {
        val message = request.queryParam("message")
            .orElseThrow { IllegalArgumentException("Message query parameter is required.") }

        return ServerResponse.ok().bodyValue(message)
    }

    fun post(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(EchoDto::class.java)
            .map { echo -> echo.copy(message = "Hello, ${echo.message}!") }
            .flatMap { updatedEcho -> ServerResponse.ok().json().body(Mono.just(updatedEcho), EchoDto::class.java) }
    }
}
