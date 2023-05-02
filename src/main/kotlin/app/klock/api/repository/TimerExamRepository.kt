package app.klock.api.repository

import app.klock.api.domain.entity.TimerExam
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TimerExamRepository : ReactiveCrudRepository<TimerExam, Long> {
  fun findAllByUserIdOrderBySeq(userId: Long): Flux<TimerExam>
}