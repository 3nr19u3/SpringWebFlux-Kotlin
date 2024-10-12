package com.lgutierrez.springboot.webflux.app.handler

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.services.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.net.URI
import java.util.*

@Component
class ProductHandler {

    @Value("\${config.uploads.path}")
    private lateinit var uploadsPath: String

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var validator: Validator

    fun list(request : ServerRequest) : Mono<ServerResponse> {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(productService.findAll(), Product::class.java)
    }

    fun show(request : ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        return productService.findById(id).flatMap {
                              ServerResponse.ok()
                              .contentType(MediaType.APPLICATION_JSON)
                              .body(fromValue(it))
        }.switchIfEmpty(ServerResponse.notFound().build())
    }

    fun create(request : ServerRequest) : Mono<ServerResponse> {
        val product: Mono<Product> = request.bodyToMono(Product::class.java)
        return product.flatMap { it ->
            //the use of errors and validations is only for course purpose because the kotlin language have
            //an more stronger typing than java(original language of this course)
            //...so this points (the that here validating) will be defined in entity class
            val errors: Errors = BeanPropertyBindingResult(it,Product::class.java.name)
            validator.validate(it, errors)

            if(errors.hasErrors()) {
                return@flatMap Flux.fromIterable(errors.fieldErrors)
                                   .map { "The field : ${it.field} - ${it.defaultMessage}" }
                                   .collectList()
                                   .flatMap { ServerResponse.badRequest().body(fromValue(it)) }

            }else{
                return@flatMap productService.save(it)
                                             .flatMap { ServerResponse.created(URI.create("/api/v2/products/${it.id}"))
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(fromValue(it))
                }
            }

        }
    }

    fun edit(request : ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        val product: Mono<Product> = request.bodyToMono(Product::class.java)
        val productRetrieved: Mono<Product> = productService.findById(id)

        return productRetrieved.zipWith(product) {
            db, req ->
                db.name = req.name
                db.price = req.price
                db.category = req.category
            return@zipWith db
        }.flatMap {
            ServerResponse.created(URI.create("/api/v2/products/${it.id}"))
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(productService.save(it), Product::class.java)
        }.switchIfEmpty(ServerResponse.notFound().build())

    }

    fun delete(request : ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        val product: Mono<Product> = productService.findById(id)

        return product.flatMap {
            productService.delete(id).then(ServerResponse.noContent().build())
        }.switchIfEmpty(ServerResponse.notFound().build())

    }

    fun upload(request : ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        return request.multipartData().map {
            it.toSingleValueMap().get("file")
        }.cast(FilePart::class.java)
            .flatMap { file ->
                productService.findById(id).flatMap {
                    it.photo = UUID.randomUUID().toString()+"-"+file.filename()
                                                                    .replace(" ", "")
                                                                    .replace(":", "")
                                                                    .replace("\\", "")

                    return@flatMap file.transferTo(File(uploadsPath+it.photo)).then(productService.save(it))
                }
            }.flatMap {
                ServerResponse.created(URI.create("/api/v2/products/${it.id}"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromValue(it))
            }.switchIfEmpty(ServerResponse.notFound().build())
    }

    fun crateCompleteProduct(request : ServerRequest) : Mono<ServerResponse> {

        val product: Mono<Product> = request.multipartData().map {
            val name: FormFieldPart = it.toSingleValueMap().get("name") as FormFieldPart
            val price: FormFieldPart = it.toSingleValueMap().get("price") as FormFieldPart
            val categoryId: FormFieldPart = it.toSingleValueMap().get("category.id") as FormFieldPart
            val categoryName: FormFieldPart = it.toSingleValueMap().get("category.name") as FormFieldPart

            val category = Category(categoryName.value())
            category.id = categoryId.value()

            return@map Product(name.value(),price.value().toDouble(),category)
        }

        return request.multipartData().map {
            it.toSingleValueMap().get("file")
        }.cast(FilePart::class.java)
            .flatMap { file ->
                product.flatMap {
                    it.photo = UUID.randomUUID().toString()+"-"+file.filename()
                        .replace(" ", "")
                        .replace(":", "")
                        .replace("\\", "")

                    return@flatMap file.transferTo(File(uploadsPath+it.photo)).then(productService.save(it))
                }
            }.flatMap {
                ServerResponse.created(URI.create("/api/v2/products/${it.id}"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromValue(it))
            }
    }

}