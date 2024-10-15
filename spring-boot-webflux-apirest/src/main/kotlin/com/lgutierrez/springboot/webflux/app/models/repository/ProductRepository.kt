package com.lgutierrez.springboot.webflux.app.models.repository

import com.lgutierrez.springboot.webflux.app.models.documents.Product
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ProductRepository : ReactiveMongoRepository<Product, String>{
    fun findByName(name: String): Mono<Product>

    // alternative for advances query1
    // fun findByNameAndPrice(name: String, price: Double): Mono<Product>

    // alternative for advances query2
    // @Query("{ 'nombre' :?0}")
    // fun findByNombre(nombre: String): Mono<Product>
}