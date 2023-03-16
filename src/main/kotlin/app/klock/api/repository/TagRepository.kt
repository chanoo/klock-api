package app.klock.api.repository

import app.klock.api.domain.entity.Tag
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : ReactiveCrudRepository<Tag, Long>
