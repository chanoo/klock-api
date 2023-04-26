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
    val username = authentication.name

    return userDetailsService.findByUsername(username)
      .flatMap { userDetails ->
        if (userDetails.isEnabled && userDetails.isAccountNonExpired && userDetails.isAccountNonLocked && userDetails.isCredentialsNonExpired) {
          Mono.just(CustomAuthenticationToken(username, authToken, userDetails.authorities))
        } else {
          Mono.empty()
        }
      }
  }
}
