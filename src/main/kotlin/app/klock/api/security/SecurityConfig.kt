package app.klock.api.security

import app.klock.api.service.CustomReactiveUserDetailsService
import app.klock.api.utils.JwtUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

// 보안 설정 클래스
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
  private val userDetailsService: CustomReactiveUserDetailsService,
  private val jwtUtils: JwtUtils // JwtUtils 주입
) {
  @Bean
  fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun customReactiveAuthenticationManager(): ReactiveAuthenticationManager {
    return CustomReactiveAuthenticationManager(userDetailsService)
  }

  @Bean
  fun jwtAuthenticationWebFilter(): JwtAuthenticationWebFilter {
    return JwtAuthenticationWebFilter(jwtUtils, customReactiveAuthenticationManager())
  }

  @Bean
  fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
      .csrf().disable()
      .httpBasic().disable()
      .formLogin().disable()
      .logout().disable()
      .authorizeExchange { exchanges ->
        exchanges
          .pathMatchers("/echo/**", "/api/auth/**", "/api/chatbots/**", "/ws/**", "/api/tags")
          .permitAll()
          .anyExchange().authenticated()
      }
      .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
      .build()
  }
}
