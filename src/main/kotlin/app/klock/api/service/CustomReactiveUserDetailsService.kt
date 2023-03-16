package app.klock.api.service

import app.klock.api.repository.AccountRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomReactiveUserDetailsService(private val accountRepository: AccountRepository) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return accountRepository.findByEmail(username)
            .map { user ->
                User.withUsername(user.username)
                    .password(user.hashedPassword)
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
