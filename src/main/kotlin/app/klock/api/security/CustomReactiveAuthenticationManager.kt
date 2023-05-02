package app.klock.api.security

import app.klock.api.service.CustomReactiveUserDetailsService
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

class CustomReactiveAuthenticationManager(
  private val userDetailsService: CustomReactiveUserDetailsService
) : ReactiveAuthenticationManager {

  override fun authenticate(authentication: Authentication): Mono<Authentication> {
    val authToken = authentication.credentials.toString()

    return userDetailsService.findByToken(authToken)
      .flatMap { userDetails ->
        if (userDetails.isEnabled && userDetails.isAccountNonExpired && userDetails.isAccountNonLocked && userDetails.isCredentialsNonExpired) {
          Mono.just(CustomAuthenticationToken(userDetails.username.toLong(), authToken, userDetails.authorities))
        } else {
          Mono.empty()
        }
      }
  }
}

