package com.lgutierrez.springboot.webflux.app.models.repository

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: ReactiveMongoRepository<Category, String> {
}