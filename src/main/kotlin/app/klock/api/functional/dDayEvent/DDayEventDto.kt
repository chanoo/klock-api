package app.klock.api.functional.dDayEvent

import java.time.LocalDate
import java.time.LocalDateTime

data class DDayEventDto(
    val id: Long? = null,
    val accountId: Long,
    val eventName: String,
    val eventDate: LocalDate,
    val createdAt: LocalDateTime
)
