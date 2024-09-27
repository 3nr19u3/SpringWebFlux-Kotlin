package com.lgutierrez.springboot.webflux.app

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.services.CategoryService
import com.lgutierrez.springboot.webflux.app.services.ProductService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.core.publisher.Flux

@SpringBootApplication
class SpringBootWebfluxApplication : CommandLineRunner {

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var categoryService: CategoryService

    private val logger = LoggerFactory.getLogger(SpringBootWebfluxApplication::class.java)

    override fun run(vararg args: String?) {
        //delete a collection each time the application is run
        reactiveMongoTemplate.dropCollection("products").subscribe()
        reactiveMongoTemplate.dropCollection("categories").subscribe()

        val electronic = Category("Electronic")
        val gym = Category("Gym")
        val tool = Category("Tool")

        Flux.just(electronic, gym, tool)
            .flatMap(categoryService::save)
            .doOnNext { category -> logger.info("Category id: ${category.id}, name: ${category.name}") }
            .thenMany(
                Flux.just(
                    Product(name = "Silla gamer",price = 500.00, category = electronic),
                    Product(name = "Monitor 32 4K", price = 1200.00, category = electronic),
                    Product(name = "Teclado ReDragon RGB",price = 100.00, category = electronic),
                    Product(name = "CPU Intel 9 - 32 RAM 2 Tb", price = 8000.00, category = electronic),
                    Product(name = "Tarjeta Grafica XXXX",price = 3200.00, category = electronic),
                    Product(name = "Escritorio Gamer", price = 2100.00, category = electronic),
                    Product(name = "Mouse G print - inalambrico",price = 800.00, category = electronic),
                    Product(name = "Pad mouse game RGB - L", price = 200.00, category = electronic),
                    Product(name = "Parlantes POOL CAST",price = 500.00, category = electronic),
                    Product(name = "Lampara LED", price = 250.00, category = electronic),
                    Product(name = "dumbbells", price = 100.00, category = gym),
                    Product(name = "Welding machine", price = 500.00, category = tool),
                    Product(name = "Screwdriver", price = 50.00, category = tool),
                )
                .flatMap { product -> productService.save(product) }

            ).subscribe { product -> logger.info("Product inserted -> $product") }


    }
}


    fun main(args: Array<String>) {
    runApplication<SpringBootWebfluxApplication>(*args)
}
