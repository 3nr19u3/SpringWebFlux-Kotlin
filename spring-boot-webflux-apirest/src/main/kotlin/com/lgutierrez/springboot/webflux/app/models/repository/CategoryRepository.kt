package com.lgutierrez.springboot.webflux.app.models.repository

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CategoryRepository: ReactiveMongoRepository<Category, String> {
    fun findByName(name: String): Mono<Category>
}