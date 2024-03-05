package app.klock.api.exception

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
@Order(-2) // WebFlux에서 기본 WebExceptionHandler보다 우선 순위를 높게 설정
class GlobalErrorHandler : WebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val path = exchange.request.uri.path
        val status = HttpStatus.BAD_REQUEST
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "path" to path,
            "status" to status.value(),
            "error" to status.reasonPhrase,
            "message" to ex.message,  // 예외 메시지를 여기에 포함
            "requestId" to exchange.request.id
        )

        exchange.response.statusCode = status
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON

        return exchange.response.writeWith(
            Mono.just(exchange.response.bufferFactory().wrap(ObjectMapper().writeValueAsBytes(errorResponse)))
        )
    }
}