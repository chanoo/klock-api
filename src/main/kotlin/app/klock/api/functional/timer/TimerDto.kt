package app.klock.api.functional.timer

import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import java.time.LocalDateTime

interface TimerDto {
  val id: Long?
  val userId: Long
  val seq: Int
  val type: String?
}

data class TimerExamDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  override val type: String = "exam",
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
      "exam",
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
  override val type: String = "pomodoro",
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
      "pomodoro",
      domain.name,
      domain.focusTime,
      domain.restTime,
      domain.cycleCount
    )
  }
}

data class TimerFocusDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  override val type: String = "focus",
  val name: String
) : TimerDto {
  fun toDomain() = TimerFocus(id, userId, seq, name)

  companion object {
    fun from(domain: TimerFocus) = TimerFocusDto(
      domain.id,
      domain.userId,
      domain.seq,
      "focus",
      domain.name
    )
  }
}
