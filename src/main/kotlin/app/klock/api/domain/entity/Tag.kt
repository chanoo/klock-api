package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("tag")
data class Tag(
    @Id
    val id: Long? = null,
    val name: String
)
