package app.klock.api.service

import app.klock.api.domain.entity.Account
import app.klock.api.repository.AccountRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * UserService 클래스는 User 엔티티와 관련된 비즈니스 로직을 처리합니다.
 * 데이터베이스 작업을 위해 UserRepository와 통신합니다.
 */
@Service
class AccountService(private val accountRepository: AccountRepository, private val passwordEncoder: PasswordEncoder) {
    /**
     * 데이터베이스에서 모든 User 엔티티를 검색합니다.
     * @return User 엔티티의 Flux를 반환합니다.
     */
    fun findAll(): Flux<Account> = accountRepository.findAll()

    /**
     * 지정된 ID를 가진 User 엔티티를 데이터베이스에서 검색합니다.
     * @param id 검색할 사용자의 ID
     * @return 검색된 사용자의 Mono를 반환하거나, 해당 ID가 없는 경우 Mono.empty()를 반환합니다.
     */
    fun findById(id: Long): Mono<Account> = accountRepository.findById(id)

    /**
     * 새로운 User 엔티티를 데이터베이스에 저장합니다.
     * @param user 저장할 사용자 정보가 포함된 User 객체
     * @return 저장된 사용자의 Mono를 반환합니다.
     */
    fun save(account: Account): Mono<Account> = accountRepository.save(account)

    /**
     * 주어진 ID에 해당하는 사용자를 찾아서 주어진 사용자 정보로 업데이트합니다.
     * @param id 업데이트할 사용자의 ID
     * @param user 업데이트할 사용자 정보를 포함한 User 객체
     * @return 업데이트된 사용자의 Mono를 반환합니다.
     */
    fun update(id: Long, user: Account): Mono<Account> =
        accountRepository.findById(id)
            .flatMap { accountRepository.save(user.copy(id = it.id, totalStudyTime = it.totalStudyTime, hashedPassword = it.hashedPassword)) }

    /**
     * 지정된 ID를 가진 User 엔티티를 데이터베이스에서 삭제합니다.
     * @param id 삭제할 사용자의 ID
     * @return 삭제 작업의 결과를 나타내는 Mono<Void>를 반환합니다.
     */
    fun deleteById(id: Long): Mono<Void> = accountRepository.deleteById(id)

    // 이메일 주소로 사용자를 검색합니다.
    fun findByEmail(email: String): Mono<Account> = accountRepository.findByEmail(email)

    // BCrypt 암호화를 사용하여 검증 합니다.
    fun validatePassword(password: String, hashedPassword: String?): Boolean = passwordEncoder.matches(password, hashedPassword)

    /**
     * 사용자의 비밀번호를 변경합니다.
     * @param id 비밀번호를 변경할 사용자의 ID
     * @param currentPassword 현재 비밀번호
     * @param password 변경할 비밀번호
     * @return 변경된 사용자의 Mono를 반환합니다.
     */
    fun changePassword(id: Long, currentPassword: String, newPassword: String): Mono<Account> =
        accountRepository.findById(id)
            .flatMap {
                if (validatePassword(currentPassword, it.hashedPassword)) {
                    val hashedPassword = passwordEncoder.encode(newPassword)
                    accountRepository.save(it.copy(hashedPassword = hashedPassword))
                } else {
                    Mono.error(IllegalArgumentException("Invalid current password"))
                }
            }


}
