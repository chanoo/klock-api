package app.klock.api.functional.timer

import app.klock.api.domain.entity.TimerExam
import app.klock.api.domain.entity.TimerFocus
import app.klock.api.domain.entity.TimerPomodoro
import java.time.LocalDateTime

interface TimerDto {
  val id: Long?
  val userId: Long
  val seq: Int
  val type: TimerType?
}

data class TimerExamDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  override val type: TimerType = TimerType.EXAM,
  val name: String,
  val startTime: LocalDateTime,
  val duration: Int,
  val questionCount: Int,
  val markingTime: Int,
) : TimerDto {
  fun toDomain() = TimerExam(id, userId, name, seq, startTime, duration, questionCount, markingTime)

  companion object {
    fun from(domain: TimerExam) = TimerExamDto(
      domain.id,
      domain.userId,
      domain.seq,
      TimerType.EXAM,
      domain.name,
      domain.startTime,
      domain.duration,
      domain.questionCount,
      domain.markingTime
    )
  }
}

data class TimerPomodoroDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  override val type: TimerType = TimerType.POMODORO,
  val name: String,
  val focusTime: Int,
  val breakTime: Int,
  val cycleCount: Int
) : TimerDto {
  fun toDomain() = TimerPomodoro(id, userId, name, seq, focusTime, breakTime, cycleCount)

  companion object {
    fun from(domain: TimerPomodoro) = TimerPomodoroDto(
      domain.id,
      domain.userId,
      domain.seq,
      TimerType.POMODORO,
      domain.name,
      domain.focusTime,
      domain.breakTime,
      domain.cycleCount
    )
  }
}

data class TimerFocusDto(
  override val id: Long? = null,
  override val userId: Long,
  override val seq: Int,
  override val type: TimerType = TimerType.FOCUS,
  val name: String
) : TimerDto {
  fun toDomain() = TimerFocus(id, userId, seq, name)

  companion object {
    fun from(domain: TimerFocus) = TimerFocusDto(
      domain.id,
      domain.userId,
      domain.seq,
      TimerType.FOCUS,
      domain.name
    )
  }
}

data class TimerSeqDto(
  val type: TimerType,
  val id: Long,
  val seq: Int
)

enum class TimerType {
  FOCUS,
  EXAM,
  POMODORO;
}
