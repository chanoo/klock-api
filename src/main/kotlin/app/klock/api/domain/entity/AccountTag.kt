package app.klock.api.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("account_tag")
data class AccountTag(
    @Id
    val id: Long? = null,
    @Column("account_id")
    val accountId: Long,
    @Column("tag_id")
    val tagId: Long
)
