package app.klock.api.functional.tag.handler

import app.klock.api.domain.entity.Tag
import app.klock.api.service.TagService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class TagHandler(private val tagService: TagService) {
    fun findAll(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body(tagService.findAll(), Tag::class.java)

    fun findById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return tagService.findById(id)
            .flatMap { tag -> ServerResponse.ok().bodyValue(tag) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    fun create(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono(Tag::class.java)
            .flatMap { tag -> tagService.create(tag) }
            .flatMap { createdTag -> ServerResponse.status(HttpStatus.CREATED).bodyValue(createdTag) }

    fun update(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return request.bodyToMono(Tag::class.java)
            .flatMap { tag -> tagService.update(id, tag) }
            .flatMap { updatedTag -> ServerResponse.ok().bodyValue(updatedTag) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    fun deleteById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toLong()
        return tagService.deleteById(id)
            .then(ServerResponse.noContent().build())
            .switchIfEmpty(ServerResponse.notFound().build())
    }
}
