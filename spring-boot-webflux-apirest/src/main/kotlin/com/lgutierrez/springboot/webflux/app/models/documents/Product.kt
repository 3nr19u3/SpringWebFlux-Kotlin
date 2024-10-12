package com.lgutierrez.springboot.webflux.app.models.documents

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.util.*


@Document(collection = "products")
data class Product (
    @Id
    val id: String? = null,
    @NotEmpty
    var name: String? = null,
    @NotNull
    var price: Double?,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val createdAt: Date? = Date(), //from java
    @Valid
    var category: Category,

    var photo: String? = null
){

    constructor() : this(name = null, price = null, createdAt = Date(), category = Category())

    constructor(name: String, price: Double) : this(name = name, price = price, createdAt = Date(), category = Category())

    constructor(name: String, price: Double, category: Category) : this(name = name, price = price, createdAt = Date(), category = category)

}