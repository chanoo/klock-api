package app.klock.api.service

import app.klock.api.aws.s3.service.S3Service
import app.klock.api.domain.entity.User
import app.klock.api.functional.user.UpdateUserRequest
import app.klock.api.functional.user.UserInfoDto
import app.klock.api.repository.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * UserService 클래스는 User 엔티티와 관련된 비즈니스 로직을 처리합니다.
 * 데이터베이스 작업을 위해 UserRepository와 통신합니다.
 */
@Service
class UserService(
  private val userRepository: UserRepository,
  private val friendRelationRepository: FriendRelationRepository,
  private val userLevelRepository: UserLevelRepository,
  private val userSettingRepository: UserSettingRepository,
  private val userTagRepository: UserTagRepository,
  private val socialLoginRepository: SocialLoginRepository,
  private val studySessionRepository: StudySessionRepository,
  private val timerExamRepository: TimerExamRepository,
  private val timerFocusRepository: TimerFocusRepository,
  private val timerPomodoroRepository: TimerPomodoroRepository,
  private val passwordEncoder: PasswordEncoder,
  private val s3Service: S3Service,
  @Value("\${cloud.aws.s3.path-user-profile}") private val userProfilePath: String,
  @Value("\${cloud.aws.s3.endpoint}") private val s3Endpoint: String
) {
  /**
   * 데이터베이스에서 모든 User 엔티티를 검색합니다.
   * @return User 엔티티의 Flux를 반환합니다.
   */
  fun findAll(): Flux<UserInfoDto> {
    return userRepository.findAll()
      .map { user ->
        UserInfoDto.from(user = user)
      }
  }

  /**
   * 지정된 ID를 가진 User 엔티티를 데이터베이스에서 검색합니다.
   * @param id 검색할 사용자의 ID
   * @return 검색된 사용자의 Mono를 반환하거나, 해당 ID가 없는 경우 Mono.empty()를 반환합니다.
   */
  @PreAuthorize("authentication.principal == #id")
  fun findById(id: Long): Mono<UserInfoDto> {
    return userRepository.findById(id)
      .flatMap { user ->
        userLevelRepository.findById(user.userLevelId)
          .flatMap { userLevel ->
            userSettingRepository.findByUserId(id)
              .flatMap { userSetting ->
                userTagRepository.findByUserId(id)
                  .map { userTag ->
                    UserInfoDto.from(user, userLevel, userSetting, userTag)
                  }
              }
          }
      }
  }

  /**
   * 새로운 User 엔티티를 데이터베이스에 저장합니다.
   * @param user 저장할 사용자 정보가 포함된 User 객체
   * @return 저장된 사용자의 Mono를 반환합니다.
   */
  @Transactional
  fun save(user: User): Mono<User> = userRepository.save(user)

  /**
   * 주어진 ID에 해당하는 사용자를 찾아서 주어진 사용자 정보로 업데이트합니다.
   * @param id 업데이트할 사용자의 ID
   * @param updateUserRequest 업데이트할 사용자 정보를 포함한 User 객체
   * @return 업데이트된 사용자의 Mono를 반환합니다.
   */
  fun update(id: Long, updateUserRequest: UpdateUserRequest): Mono<UserInfoDto> {
    return userRepository.findById(id)
      .flatMap { user ->
        userRepository.save(
          user.copy(
            nickname = updateUserRequest.nickname,
            updatedAt = updateUserRequest.updatedAt
          )
        )
          .flatMap { updateUser ->
            userSettingRepository.findByUserId(id)
              .flatMap { userSetting ->
                userSettingRepository.save(
                  userSetting.copy(
                    startOfTheDay = updateUserRequest.startOfTheDay,
                    startOfTheWeek = updateUserRequest.startOfTheWeek
                  )
                )
                  .flatMap { updateUserSetting ->
                    userTagRepository.findByUserId(id)
                      .flatMap { userTag ->
                        userTagRepository.save(
                          userTag.copy(
                            tagId = updateUserRequest.tagId
                          )
                        )
                          .map { updateTag ->
                            UserInfoDto.from(user = updateUser, userSetting = updateUserSetting, userTag = updateTag)
                          }
                      }
                  }
              }
          }
      }
  }

  /**
   * 사용자 탈퇴 처리
   * @param id 탈퇴처리할 사용자의 ID
   *
   * user 엔티티
   * - nickname = #id
   * - email = null
   * - hashedPassword = null
   * - active = false
   * userSocialLogin 엔티티
   * - 삭제
   * userSetting 엔티티
   * - 삭제
   * userTag 엔티티
   * - 삭제
   * studySession 엔티티
   * - 삭제
   * timerExam/timerFocus/timerPomodoro 엔티티
   * - 삭제
   * friendRelation 엔티티
   * - ???
   *
   * @return 탈퇴 작업의 결과를 나타내는 Mono<Void>를 반환합니다.
   */
  @PreAuthorize("authentication.principal == #id")
  fun deleteById(id: Long): Mono<Void> {
    val deleteSocialLogin = Mono.defer { socialLoginRepository.deleteByUserId(id) }
    val deleteUserSetting = Mono.defer { userSettingRepository.deleteByUserId(id) }
    val deleteUserTag = Mono.defer { userTagRepository.deleteByUserId(id) }
    val deleteStudySession = Mono.defer { studySessionRepository.deleteByUserId(id) }
    val deleteTimerExam = Mono.defer { timerExamRepository.deleteByUserId(id) }
    val deleteTimerFocus = Mono.defer { timerFocusRepository.deleteByUserId(id) }
    val deleteTimerPomodoro = Mono.defer { timerPomodoroRepository.deleteByUserId(id) }

    return Mono.`when`(
      deleteSocialLogin,
      deleteUserSetting,
      deleteUserTag,
      deleteStudySession,
      deleteTimerExam,
      deleteTimerFocus,
      deleteTimerPomodoro
    ).then(
      userRepository.findById(id)
        .flatMap { user ->
          userRepository.save(
            user.copy(
              nickname = "#$id",
              email = null,
              hashedPassword = null,
              active = false,
              updatedAt = LocalDateTime.now()
            )
          )
        }
        .then(Mono.empty())
    )
  }

  // 이메일 주소로 사용자를 검색합니다.
  fun findByEmail(email: String): Mono<User> = userRepository.findByEmail(email)

  // BCrypt 암호화를 사용하여 검증 합니다.
  fun validatePassword(password: String, hashedPassword: String?): Boolean =
    passwordEncoder.matches(password, hashedPassword)

  /**
   * 사용자의 비밀번호를 변경합니다.
   * @param id 비밀번호를 변경할 사용자의 ID
   * @param currentPassword 현재 비밀번호
   * @param newPassword 변경할 비밀번호
   * @return 변경된 사용자의 Mono를 반환합니다.
   */
  @Transactional
  fun changePassword(id: Long, currentPassword: String, newPassword: String): Mono<User> =
    userRepository.findById(id)
      .flatMap {
        if (validatePassword(currentPassword, it.hashedPassword)) {
          val hashedPassword = passwordEncoder.encode(newPassword)
          userRepository.save(it.copy(hashedPassword = hashedPassword))
        } else {
          Mono.error(IllegalArgumentException("Invalid current password"))
        }
      }

  fun existedNickname(nickname: String): Mono<Boolean> {
    if (!validateNickname(nickname)) {
      return Mono.error(IllegalArgumentException("Invalid nick name"))
    }
    return userRepository.findByNickname(nickname)
      .hasElement()
  }

  private fun validateNickname(nickname: String): Boolean {
    return nickname.isNotEmpty() &&
      nickname.length <= User.allowedNicknameMaxLength() &&
      User.allowedPattern().matches(nickname)
  }

  fun updateProfileImage(userId: Long, imageBytes: ByteArray, originFileName: String): Mono<User> {
    return userRepository.findById(userId)
      .flatMap { user ->
        s3Service.uploadFile(userProfilePath, imageBytes, originFileName)
          .flatMap { key ->
            userRepository.save(
              user.copy(
                profileImage = "$s3Endpoint/$key"
              )
            )
          }
      }
  }

  fun searchByNickname(userId: Long, nickname: String): Mono<User> {
    if (!validateNickname(nickname)) {
      return Mono.error(IllegalArgumentException("친구 닉네임을 확인해주세요"))
    }

    return userRepository.findByNickname(nickname)
      .flatMap { user ->
        val followId = user.id ?: return@flatMap Mono.error(IllegalArgumentException("친구 닉네임을 확인해주세요"))
        if (userId == followId) {
          return@flatMap Mono.error(IllegalArgumentException("자기 자신을 친구로 추가할 수 없어요"))
        }

        friendRelationRepository.findByUserIdAndFollowId(userId, followId)
          .flatMap<User> { friendRelation ->
            when {
              friendRelation.followed -> Mono.error(RuntimeException("이미 친구예요"))
              else -> Mono.error(RuntimeException("이미 친구 요청을 보냈어요"))
            }
          }
          .switchIfEmpty(Mono.defer { Mono.just(user) }) // 팔로우 관계가 없는 경우 사용자 정보 반환
      }
  }
}
