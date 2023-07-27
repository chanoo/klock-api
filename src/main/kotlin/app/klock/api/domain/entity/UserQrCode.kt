package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * UserQrCode 엔티티는 사용자 QR 코드 정보를 저장하는 클래스입니다.
 * @property id 데이터베이스에서 자동 생성되는 기본 키 값
 * @property userId 사용자의 고유 식별자
 * @property qrCode QR CODE 데이터
 * @property encryptKey QR코드 암호키
 * @property expiredAt QR코드 유효기간
 */
@Table("klk_user_qrcode")
data class UserQrCode(
  @Id
  val id: Long? = null,

  @Column("user_id")
  val userId: Long,

  @Column("qr_code")
  val qrCode: String,

  @Column("encrypt_key")
  val encryptKey: String,

  @Column("expired_at")
  val expiredAt: LocalDateTime
)
