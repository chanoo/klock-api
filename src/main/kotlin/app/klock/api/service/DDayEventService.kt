package app.klock.api.service

import app.klock.api.domain.entity.DDayEvent
import app.klock.api.functional.dDayEvent.DDayEventDto
import app.klock.api.repository.DDayEventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class DDayEventService(private val repository: DDayEventRepository) {

    fun getByAccountId(accountId: Long): Mono<List<DDayEventDto>> {
        return repository.findByAccountId(accountId)
            .map { DDayEventDto(it.id, it.accountId, it.eventName, it.eventDate, it.createdAt) }
            .collectList()
    }

    fun getById(id: Long): Mono<DDayEventDto> {
        return repository.findById(id)
            .map { DDayEventDto(it.id, it.accountId, it.eventName, it.eventDate, it.createdAt) }
    }

    @Transactional
    fun create(event: DDayEventDto): Mono<DDayEventDto> {
        return repository.save(DDayEvent(null, event.accountId, event.eventName, event.eventDate, event.createdAt))
            .map { DDayEventDto(it.id, it.accountId, it.eventName, it.eventDate, it.createdAt) }
    }

    @Transactional
    fun update(id: Long, event: DDayEventDto): Mono<DDayEventDto> {
        return repository.findById(id)
            .flatMap {
                val updatedEvent = it.copy(
                    accountId = event.accountId,
                    eventName = event.eventName,
                    eventDate = event.eventDate,
                    createdAt = event.createdAt
                )
                repository.save(updatedEvent)
            }
            .map { DDayEventDto(it.id, it.accountId, it.eventName, it.eventDate, it.createdAt) }
    }

    @Transactional
    fun delete(id: Long): Mono<Void> {
        return repository.deleteById(id)
    }
}
