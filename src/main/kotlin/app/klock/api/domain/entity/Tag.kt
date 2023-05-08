package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * Tag 엔티티는 태그 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property name 태그 이름
 */
@Table("klk_tag")
data class Tag(
  @Id
  val id: Long? = null,

  val name: String
)
