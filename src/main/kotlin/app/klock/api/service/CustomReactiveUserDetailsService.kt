package app.klock.api.service

import app.klock.api.repository.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.lang.Long.parseLong

@Service
class CustomReactiveUserDetailsService(private val userRepository: UserRepository) : ReactiveUserDetailsService {

  override fun findByUsername(username: String): Mono<UserDetails> {
    return userRepository.findById(parseLong(username))
      .map { user ->
        User.withUsername(username)
          .password("!password!")
          .authorities(user.role.name)
          .accountExpired(!user.active)
          .accountLocked(!user.active)
          .credentialsExpired(!user.active)
          .disabled(!user.active)
          .build()
      }
      .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found: $username")))
  }
}
