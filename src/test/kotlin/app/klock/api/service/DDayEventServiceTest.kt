package app.klock.api.service

import app.klock.api.domain.entity.DDayEvent
import app.klock.api.functional.dDayEvent.DDayEventDto
import app.klock.api.repository.DDayEventRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@SpringBootTest(classes = [DDayEventService::class])
class DDayEventServiceTest @Autowired constructor(
    private val service: DDayEventService
) {
    @MockBean
    private lateinit var repository: DDayEventRepository

    @Test
    fun `id로 D-Day 이벤트 가져오기`() {
        val event = DDayEvent(1L, 100L, "테스트 이벤트", LocalDate.now(), LocalDateTime.now())
        Mockito.`when`(repository.findById(1L)).thenReturn(Mono.just(event))

        val result = service.getById(1L)

        StepVerifier.create(result)
            .expectNext(DDayEventDto(1L, 100L, "테스트 이벤트", event.eventDate, event.createdAt))
            .verifyComplete()
    }

    @Test
    fun `D-Day 이벤트 생성하기`() {
        val eventDto = DDayEventDto(null, 100L, "테스트 이벤트", LocalDate.now(), LocalDateTime.now())
        val event = DDayEvent(1L, 100L, "테스트 이벤트", eventDto.eventDate, eventDto.createdAt)
        Mockito.`when`(repository.save(any(DDayEvent::class.java))).thenReturn(Mono.just(event))

        val result = service.create(eventDto)

        StepVerifier.create(result)
            .expectNext(DDayEventDto(1L, 100L, "테스트 이벤트", event.eventDate, event.createdAt))
            .verifyComplete()
    }

    @Test
    fun `D-Day 이벤트 업데이트하기`() {
        val eventDto = DDayEventDto(1L, 100L, "업데이트된 테스트 이벤트", LocalDate.now(), LocalDateTime.now())
        val event = DDayEvent(1L, 100L, "테스트 이벤트", eventDto.eventDate, eventDto.createdAt)
        val updatedEvent = DDayEvent(1L, 100L, "업데이트된 테스트 이벤트", eventDto.eventDate, eventDto.createdAt)
        Mockito.`when`(repository.findById(1L)).thenReturn(Mono.just(event))
        Mockito.`when`(repository.save(any(DDayEvent::class.java))).thenReturn(Mono.just(updatedEvent))

        val result = service.update(1L, eventDto)

        StepVerifier.create(result)
            .expectNext(DDayEventDto(1L, 100L, "업데이트된 테스트 이벤트", event.eventDate, event.createdAt))
            .verifyComplete()
    }
    @Test
    fun `D-Day 이벤트 삭제하기`() {
        Mockito.`when`(repository.deleteById(1L)).thenReturn(Mono.empty<Void>())

        val result = service.delete(1L)

        StepVerifier.create(result)
            .verifyComplete()
    }
}
