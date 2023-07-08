package app.klock.api.functional.deploy

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class DeployHandler() {

  fun ready(request: ServerRequest): Mono<ServerResponse> {
    return ServerResponse.ok().build()
  }

  fun healthy(request: ServerRequest): Mono<ServerResponse> {
    return ServerResponse.ok().build()
  }

}
