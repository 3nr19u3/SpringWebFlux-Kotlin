package com.lgutierrez.springboot.webflux.app.models

import java.util.*

data class Product (
    val id: String? = null,
    var name: String? = null,
    var price: Double?,
    val createdAt: Date? = Date(), //from java
    var category: Category,
    var photo: String? = null
){
}