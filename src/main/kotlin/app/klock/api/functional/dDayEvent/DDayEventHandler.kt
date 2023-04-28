package app.klock.api.functional.handler

import app.klock.api.functional.dDayEvent.DDayEventDto
import app.klock.api.service.DDayEventService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class DDayEventHandler(private val dDayEventService: DDayEventService) {

  // GET /api/d-day?userId={userId}
  suspend fun getByUserId(request: ServerRequest): ServerResponse {
    val userId = request.queryParam("userId").get().toLong()
    val events = dDayEventService.getByUserId(userId)
    return ServerResponse.ok().json().bodyValueAndAwait(events)
  }

  // GET /api/d-day/{id}
  suspend fun getById(request: ServerRequest): ServerResponse {
    val id = request.pathVariable("id").toLong()
    val event = dDayEventService.getById(id)
    return ServerResponse.ok().json().bodyValueAndAwait(event)
  }

  // POST /api/d-day
  suspend fun create(request: ServerRequest): ServerResponse {
    val event = request.awaitBody<DDayEventDto>()
    val createdEvent = dDayEventService.create(event)
    return ServerResponse.ok().json().bodyValueAndAwait(createdEvent)
  }

  // PUT /api/d-day/{id}
  suspend fun update(request: ServerRequest): ServerResponse {
    val id = request.pathVariable("id").toLong()
    val event = request.awaitBody<DDayEventDto>()
    val updatedEvent = dDayEventService.update(id, event)
    return ServerResponse.ok().json().bodyValueAndAwait(updatedEvent)
  }

  // DELETE /api/d-day/{id}
  suspend fun delete(request: ServerRequest): ServerResponse {
    val id = request.pathVariable("id").toLong()
    dDayEventService.delete(id)
    return ServerResponse.noContent().buildAndAwait()
  }
}
