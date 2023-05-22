package app.klock.api.domain.entity

import app.klock.api.functional.timer.TimerType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * StudySession 엔티티는 사용자의 공부 세션 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 해당 사용자의 고유 식별자
 * @property startTime 공부 세션의 시작 시간
 * @property endTime 공부 세션의 종료 시간
 */
@Table("klk_study_session")
data class StudySession(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("start_time")
  val startTime: LocalDateTime,

  @Column("end_time")
  val endTime: LocalDateTime,

  @Column("timer_name")
  val timerName: String,

  @Column("timer_type")
  val timerType: TimerType
)
