package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.DayOfWeek
import java.time.temporal.WeekFields

/**
 * UserSetting 엔티티는 사용자의 셋팅정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 해당 사용자의 고유 식별자
 * @property startOfTheWeek 한주의 시작 요일
 * @property startOfTheDay 하루의 시작 일시
 */
@Table("klk_user_setting")
data class UserSetting(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("start_of_the_week")
  val startOfTheWeek: DayOfWeek,

  @Column("start_of_the_day")
  val startOfTheDay: Int
)
