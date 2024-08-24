package com.lgutierrez.springboot.webflux.app.models.documents

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


@Document(collection = "products")
data class Product (
    @Id
    val id: String? = null,
    val name: String = "",
    val price: Double = 0.00,
    val createdAt: Date? = Date() //from java
){

    constructor(name: String, price: Double) : this(name = name, price = price, createdAt = Date())

}