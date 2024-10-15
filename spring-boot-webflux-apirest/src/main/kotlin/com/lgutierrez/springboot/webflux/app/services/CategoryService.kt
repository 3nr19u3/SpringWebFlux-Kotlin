package com.lgutierrez.springboot.webflux.app.services

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CategoryService {

    fun findAll(): Flux<Category>

    fun findById(id: String): Mono<Category>

    fun save(category: Category): Mono<Category>

    fun findByName(name: String): Mono<Category>

}