package app.klock.api.security

import app.klock.api.utils.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

// JWT를 사용하여 인증을 처리하는 웹 필터
class JwtAuthenticationWebFilter(
    jwtUtils: JwtUtils,
    authenticationManager: ReactiveAuthenticationManager
) : AuthenticationWebFilter(authenticationManager) {

    init {
        // 인증 컨버터 설정
        setServerAuthenticationConverter { exchange: ServerWebExchange ->
            val token = exchange.request.headers.getFirst("Authorization")
            if (token != null && token.startsWith("Bearer ")) {
                val authToken = token.substring(7)
                if (jwtUtils.validateToken(authToken)) {
                    val username = jwtUtils.getUserIdFromToken(authToken)
                    val authorities = jwtUtils.getAuthoritiesFromJwt(authToken)
                    // JWT 토큰의 사용자 이름 및 권한을 사용하여 인증 토큰 생성
                    return@setServerAuthenticationConverter Mono.just(UsernamePasswordAuthenticationToken(username, null, authorities))
                }
            }
            // 토큰이 없거나 유효하지 않으면 Mono.empty() 반환
            return@setServerAuthenticationConverter Mono.empty()
        }

        // 보안 컨텍스트 저장소 설정
        setSecurityContextRepository(object : ServerSecurityContextRepository {
            private val repository = WebSessionServerSecurityContextRepository()

            override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> {
                return repository.save(exchange, context)
            }

            override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
                return repository.load(exchange)
                    .doOnNext { ReactiveSecurityContextHolder.withSecurityContext(Mono.just(it)) }
            }
        })

        // 인증 실패 핸들러 설정
        setAuthenticationFailureHandler(
            ServerAuthenticationEntryPointFailureHandler { exchange, _ ->
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                Mono.empty()
            }
        )
    }
}
