package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DDayEvent 엔티티는 사용자의 D-Day 이벤트 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 해당 사용자의 고유 식별자
 * @property eventName D-Day 이벤트의 이름
 * @property eventDate D-Day 이벤트의 날짜
 * @property createdAt 레코드 생성 시간
 */
@Table("klk_d_day_event")
data class DDayEvent(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("event_name")
  val eventName: String,

  @Column("event_date")
  val eventDate: LocalDate,

  @Column("created_at")
  val createdAt: LocalDateTime
)
