package app.klock.api.functional.echo

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class EchoHandler {

    fun echo(request: ServerRequest): Mono<ServerResponse> {
        val message = request.queryParam("message")
            .orElseThrow { IllegalArgumentException("Message query parameter is required.") }

        return ServerResponse.ok().bodyValue(message)
    }
}
