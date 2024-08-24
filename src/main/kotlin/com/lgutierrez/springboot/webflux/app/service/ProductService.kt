package com.lgutierrez.springboot.webflux.app.service

import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.models.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductService {

    @Autowired
    private lateinit var productDao : ProductRepository

    fun save(product: Product){
        //TODO something ...
        productDao.save(product)
    }
}