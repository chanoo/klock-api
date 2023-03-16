package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("account_level")
data class AccountLevel(
    @Id
    val id: Long? = null,
    val level: Int,
    @Column("required_study_time")
    val requiredStudyTime: Int,
    @Column("character_name")
    val characterName: String,
    @Column("character_image")
    val characterImage: String
)
