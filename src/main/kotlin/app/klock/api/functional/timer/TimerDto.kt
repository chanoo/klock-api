package app.klock.api.functional.timer

import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import java.time.LocalDateTime

interface TimerDto {
  val id: Long?
  val userId: Long
  val seq: Int
}

data class TimerExamDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  val name: String,
  val startTime: LocalDateTime,
  val duration: Int,
  val questionCount: Int,
) : TimerDto {
  fun toDomain() = TimerExam(id, userId, name, seq, startTime, duration, questionCount)

  companion object {
    fun from(domain: TimerExam) = TimerExamDto(
      domain.id,
      domain.userId,
      domain.seq,
      domain.name,
      domain.startTime,
      domain.duration,
      domain.questionCount
    )
  }
}

data class TimerPomodoroDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  val name: String,
  val focusTime: Int,
  val restTime: Int,
  val cycleCount: Int
) : TimerDto {
  fun toDomain() = TimerPomodoro(id, userId, name, seq, focusTime, restTime, cycleCount)

  companion object {
    fun from(domain: TimerPomodoro) = TimerPomodoroDto(
      domain.id,
      domain.userId,
      domain.seq,
      domain.name,
      domain.focusTime,
      domain.restTime,
      domain.cycleCount
    )
  }

  fun validate() {
    require(name.isNotBlank()) { "name must not be blank" }
    require(focusTime > 0) { "focusTime must be greater than 0" }
    require(restTime > 0) { "restTime must be greater than 0" }
    require(cycleCount > 0) { "cycleCount must be greater than 0" }
  }
}

data class TimerFocusDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  val name: String
) : TimerDto {
  fun toDomain() = TimerFocus(id, userId, seq, name)

  companion object {
    fun from(domain: TimerFocus) = TimerFocusDto(
      domain.id,
      domain.userId,
      domain.seq,
      domain.name
    )
  }
}
