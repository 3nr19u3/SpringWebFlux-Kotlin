package com.lgutierrez.springboot.webflux.app

import com.lgutierrez.springboot.webflux.app.models.repository.ProductRepository
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Flux

@SpringBootApplication
class SpringBootWebfluxApplication : CommandLineRunner {

    @Autowired
    private lateinit var productRepository: ProductRepository

    private val logger = LoggerFactory.getLogger(SpringBootWebfluxApplication::class.java)

    override fun run(vararg args: String?) {
        //TODO("Not yet implemented")
        Flux.just(
                    Product(name = "Silla gamer",price = 500.00),
                    Product(name = "Monitor 32 4K", price = 1200.00)
                ).flatMap {product -> productRepository.save(product) }
                 .subscribe { product -> logger.info(String.format("Inserted: %s", product.toString())) }


    }
}


    fun main(args: Array<String>) {
    runApplication<SpringBootWebfluxApplication>(*args)
}
