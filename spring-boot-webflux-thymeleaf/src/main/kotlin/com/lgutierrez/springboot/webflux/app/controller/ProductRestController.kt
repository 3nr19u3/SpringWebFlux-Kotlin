package com.lgutierrez.springboot.webflux.app.controller

import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.services.impl.ProductServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/products")
class ProductRestController {

    @Autowired
    private lateinit var productService: ProductServiceImpl

    private val logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping
    fun index(): Flux<Product>{
        return productService.findAll().doOnNext { p -> logger.info(p.name) }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<Product> {
        //return productService.findById(id)
        val products: Flux<Product> = productService.findAll()

        return products.filter{ p -> p.id.equals(id)}.next()
                                         .doOnNext { p -> logger.info(p.name) }
    }

}