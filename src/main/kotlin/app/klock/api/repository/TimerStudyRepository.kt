package app.klock.api.repository

import app.klock.api.domain.entity.TimerStudy
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TimerStudyRepository : ReactiveCrudRepository<TimerStudy, Long>
