package com.lgutierrez.springboot.webflux.app.services

import com.lgutierrez.springboot.webflux.app.models.Product
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductService {

    fun findAll(): Flux<Product>

    fun findById(id: String): Mono<Product>

    fun create(product: Product): Mono<Product>

    fun update(product: Product, id: String): Mono<Product>

    fun delete(id: String): Mono<Void>

    fun upload(FilePart: FilePart, id: String): Mono<Product>
}