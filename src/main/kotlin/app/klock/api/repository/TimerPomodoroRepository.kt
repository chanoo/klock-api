package app.klock.api.repository

import app.klock.api.domain.entity.TimerPomodoro
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TimerPomodoroRepository : ReactiveCrudRepository<TimerPomodoro, Long>
