package app.klock.api.domain.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("account")
data class Account(
    @Id
    val id: Long? = null,
    val email: String,
    @Column("hashed_password")
    var hashedPassword: String? = null,
    val username: String,
    @Column("total_study_time")
    val totalStudyTime: Int,
    @Column("account_level_id")
    val accountLevelId: Long,
    val role: AccountRole,
    val active: Boolean,
    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime,
    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: LocalDateTime
)
