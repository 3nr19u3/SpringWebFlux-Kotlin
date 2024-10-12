package com.lgutierrez.springboot.webflux.app.models.documents


import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "categories")
data class Category (
    @Id
    @NotEmpty
    var id: String? = null,
    @NotNull
    val name: String = ""
){
    constructor(name: String): this(id = null, name = name)
}