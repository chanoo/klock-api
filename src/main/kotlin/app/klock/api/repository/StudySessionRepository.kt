package app.klock.api.repository

import app.klock.api.domain.entity.StudySession
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
interface StudySessionRepository : ReactiveCrudRepository<StudySession, Long> {

    // userid와 startTime 으로 studySession 찾기
    fun findByAccountIdAndStartTimeBetween(userId: Long, start: LocalDateTime, end: LocalDateTime): Flux<StudySession>
}
