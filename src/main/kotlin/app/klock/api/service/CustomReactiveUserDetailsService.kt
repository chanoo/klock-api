package app.klock.api.service

import app.klock.api.repository.UserRepository
import app.klock.api.utils.JwtUtils
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomReactiveUserDetailsService(
  private val userRepository: UserRepository,
  private val jwtUtils: JwtUtils
) : ReactiveUserDetailsService {

  override fun findByUsername(username: String): Mono<UserDetails> {
    return userRepository.findById(username.toLong())
      .map(::convertToUserDetails)
      .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found: $username")))
  }

  fun findByToken(token: String): Mono<UserDetails> {
    val username = jwtUtils.getUserIdFromToken(token)
    return findByUsername(username)
  }

  private fun convertToUserDetails(user: app.klock.api.domain.entity.User): UserDetails {
    return User.withUsername(user.id.toString())
      .password("!password!")
      .authorities(user.role.name)
      .accountExpired(!user.active)
      .accountLocked(!user.active)
      .credentialsExpired(!user.active)
      .disabled(!user.active)
      .build()
  }
}
