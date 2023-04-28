package app.klock.api.repository

import app.klock.api.domain.entity.DDayEvent
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface DDayEventRepository : ReactiveCrudRepository<DDayEvent, Long> {
  fun findByUserId(userId: Long): Flux<DDayEvent>
}

