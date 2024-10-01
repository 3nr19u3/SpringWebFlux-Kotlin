package com.lgutierrez.springboot.webflux.app.controller

import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.services.impl.ProductServiceImpl
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.cast
import java.io.File
import java.net.URI
import java.util.Date
import java.util.Objects
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/products")
class ProductController {

    @Autowired
    private lateinit var productService: ProductServiceImpl

    @Value("\${config.uploads.path}")
    private lateinit var uploadsPath: String

    @GetMapping
    fun findAll(): Mono<ResponseEntity<Flux<Product>>> {
        return Mono.just(
            ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll())
        )
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: String): Mono<ResponseEntity<Product>> {
        return productService.findById(id).map {
            ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(it)
        }.defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @PostMapping
    fun create(@Valid @RequestBody monoProduct: Mono<Product>): Mono<ResponseEntity<HashMap<String, Any>>> {
        val resp = HashMap<String, Any>()
        return monoProduct.flatMap {
            return@flatMap productService.save(it).map {
                    resp.put("product", it)
                    resp.put("message","product created successfully")
                    resp.put("timestamp", Date())
                return@map ResponseEntity.created(URI.create("/api/products/${it.id}"))
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .body(resp)
            }
        }.onErrorResume { it ->
            return@onErrorResume Mono.just(it).cast(WebExchangeBindException::class.java)
                .flatMap { Mono.just(it.fieldErrors) }
                .flatMapMany { Flux.fromIterable(it) }
                .map { "The field : ${it.field} - ${it.defaultMessage}" }
                .collectList()
                .flatMap {
                    resp.put("errors", it)
                    resp.put("timestamp", Date())
                    resp.put("status", HttpStatus.BAD_REQUEST.value())
                    return@flatMap Mono.just(ResponseEntity.badRequest().body(resp))
                }
        }
    }

    @PutMapping("/{id}")
    fun update(@RequestBody product: Product, @PathVariable("id") id: String): Mono<ResponseEntity<Product>> {
        return productService.findById(id).flatMap {
            it.name = product.name
            it.price = product.price
            it.category = product.category
            return@flatMap productService.save(it)
        }.map {
            ResponseEntity.created(URI.create("/api/products/${it.id}"))
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(it)
        }.defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): Mono<ResponseEntity<Void>> {
        return productService.findById(id).flatMap {
            return@flatMap productService.delete(id).then(Mono.just(ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
        }.defaultIfEmpty(ResponseEntity<Void>(HttpStatus.NOT_FOUND))
    }

    @PostMapping("/upload/{id}")
    fun upload(@PathVariable("id") id: String, @RequestPart file: FilePart): Mono<ResponseEntity<Product>> {
        return productService.findById(id).flatMap {
            it.photo = (UUID.randomUUID().toString() + "-" + file.filename()
                        .replace(" ", "")
                        .replace(":","")
                        .replace("\\",""))

            return@flatMap file.transferTo(File(uploadsPath+it.photo)).then(productService.save(it))

        }.map { ResponseEntity.ok(it) }
        .defaultIfEmpty(ResponseEntity.notFound().build())
    }



}