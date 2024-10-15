package com.lgutierrez.springboot.webflux.app.services.impl

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.models.repository.CategoryRepository
import com.lgutierrez.springboot.webflux.app.services.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CategoryServiceImpl: CategoryService {

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    override fun findAll(): Flux<Category> {
        return categoryRepository.findAll()
    }

    override fun findById(id: String): Mono<Category> {
        return categoryRepository.findById(id)
    }

    override fun save(category: Category): Mono<Category> {
        return categoryRepository.save(category)
    }

    override fun findByName(name: String): Mono<Category> {
        return categoryRepository.findByName(name)
    }
}