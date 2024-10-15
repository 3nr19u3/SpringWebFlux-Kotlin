package com.lgutierrez.springboot.webflux.app.services.impl

import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.models.repository.ProductRepository
import com.lgutierrez.springboot.webflux.app.services.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProductServiceImpl : ProductService {

    @Autowired
    private lateinit var productRepository : ProductRepository

    override fun findAll(): Flux<Product> {
        return productRepository.findAll()
    }

    override fun findById(id: String): Mono<Product> {
        return productRepository.findById(id)
    }

    override fun save(product: Product): Mono<Product> {
        return productRepository.save(product)
    }

    override fun delete(id: String): Mono<Void> {
        return productRepository.deleteById(id)
    }

    override fun findAllWithUppercaseNames(): Flux<Product> {
        //to modify the name attribute to uppercase
        return productRepository.findAll().map { product ->
            product.copy(name = product.name?.uppercase())
        }
    }

    override fun findAllWithUppercaseNamesRepeat(num: Long): Flux<Product> {
        //repeats the given number of times
        return findAllWithUppercaseNames().repeat(num)
    }

    override fun findByName(name: String): Mono<Product> {
        return productRepository.findByName(name)
    }
}