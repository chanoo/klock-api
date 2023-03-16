package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("study_session")
data class StudySession(
    @Id
    val id: Long? = null,
    @Column("account_id")
    val accountId: Long,
    @Column("start_time")
    val startTime: LocalDateTime,
    @Column("end_time")
    val endTime: LocalDateTime
)
