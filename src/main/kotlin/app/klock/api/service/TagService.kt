package app.klock.api.service

import app.klock.api.domain.entity.Tag
import app.klock.api.repository.TagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagService(private val tagRepository: TagRepository) {
    fun findAll(): Flux<Tag> = tagRepository.findAll()

    fun findById(id: Long): Mono<Tag> = tagRepository.findById(id)

    fun create(tag: Tag): Mono<Tag> = tagRepository.save(tag)

    fun update(id: Long, tag: Tag): Mono<Tag> =
        tagRepository.findById(id)
            .flatMap { existingTag ->
                val updatedTag = existingTag.copy(name = tag.name)
                tagRepository.save(updatedTag)
            }

    fun deleteById(id: Long): Mono<Void> = tagRepository.deleteById(id)
}
