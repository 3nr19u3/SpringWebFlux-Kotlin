package com.lgutierrez.springboot.webflux.app.services
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductService {

    fun findAll(): Flux<Product>

    fun findById(id: String): Mono<Product>

    fun save(product: Product): Mono<Product>

    fun delete(id: String): Mono<Void>

    fun findAllWithUppercaseNames(): Flux<Product>

    fun findAllWithUppercaseNamesRepeat(num: Long): Flux<Product>

}