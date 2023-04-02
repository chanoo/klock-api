package app.klock.api.domain.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("social_login")
data class SocialLogin(
    @Id
    val id: Long? = null,
    @Column("provider")
    val provider: SocialProvider,
    @Column("provider_user_id")
    val providerUserId: String,
    @Column("account_id")
    val accountId: Long,
    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
