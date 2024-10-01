package com.lgutierrez.springboot.webflux.app.models.repository

import com.lgutierrez.springboot.webflux.app.models.documents.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : ReactiveMongoRepository<Product, String>{
}