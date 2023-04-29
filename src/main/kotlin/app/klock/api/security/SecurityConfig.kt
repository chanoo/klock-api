package app.klock.api.security

import app.klock.api.service.CustomReactiveUserDetailsService
import app.klock.api.utils.JwtUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

// 보안 설정 클래스
@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
  private val userDetailsService: CustomReactiveUserDetailsService,
  private val jwtUtils: JwtUtils // JwtUtils 주입
) {

  // 패스워드 인코더 빈 생성
  @Bean
  fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun customReactiveAuthenticationManager(): ReactiveAuthenticationManager {
    return CustomReactiveAuthenticationManager(userDetailsService)
  }


  // JwtAuthenticationWebFilter 빈 생성
  @Bean
  fun jwtAuthenticationWebFilter(): JwtAuthenticationWebFilter {
    return JwtAuthenticationWebFilter(jwtUtils, customReactiveAuthenticationManager())
  }

  // 보안 웹 필터 체인 설정
  @Bean
  fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
      .csrf().disable() // CSRF 보호 비활성화
      .httpBasic().disable() // HTTP Basic 인증 비활성화
      .formLogin().disable() // 폼 기반 로그인 비활성화
      .logout().disable() // 로그아웃 비활성화
      .authorizeExchange { exchanges ->
        exchanges
          .pathMatchers("/echo/**", "/api/auth/**", "/api/chatbots/**", "/ws/**", "/api/tags").permitAll() // 인증이 필요 없는 경로
          .pathMatchers("/api/pomodoro-timers/**", "/api/exam-timers/**", "/api/focus-timers/**").permitAll() // 인증이 필요 없는 경로
          .anyExchange().authenticated() // 나머지 경로는 인증 필요
      }
      .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION) // JWT 인증 필터 추가
      .build()
  }

}
