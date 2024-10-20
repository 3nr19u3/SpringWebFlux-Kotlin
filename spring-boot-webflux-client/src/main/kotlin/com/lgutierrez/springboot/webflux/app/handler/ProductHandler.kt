package com.lgutierrez.springboot.webflux.app.handler

import com.lgutierrez.springboot.webflux.app.models.Product
import com.lgutierrez.springboot.webflux.app.services.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.net.URI
import java.util.*

@Component
class ProductHandler {

    @Autowired
    private lateinit var productService: ProductService

    fun list(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
                             .contentType(APPLICATION_JSON)
                             .body(productService.findAll(), Product::class.java)
    }


    fun show(request: ServerRequest): Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        return productService.findById(id)
                             .flatMap { product ->
                                 ServerResponse.ok()
                                               .contentType(APPLICATION_JSON)
                                               .body(fromValue(product))
                                     .switchIfEmpty(ServerResponse.notFound().build())
                                     .onErrorResume {
                                         val errorResponse: WebClientResponseException = it as WebClientResponseException
                                         if(errorResponse.statusCode == HttpStatus.NOT_FOUND){
                                             val resp = HashMap<String, Any>()
                                             resp.put("error","Product not found: ${errorResponse.message}")
                                             resp.put("timestamp",Date())
                                             resp.put("status",errorResponse.statusCode)

                                             return@onErrorResume ServerResponse.status(HttpStatus.NOT_FOUND).body(fromValue(resp))
                                         }

                                         return@onErrorResume Mono.error(errorResponse)
                                     }
                             }
    }

    fun create(request : ServerRequest) : Mono<ServerResponse> {
        val product: Mono<Product> = request.bodyToMono(Product::class.java)
        return product.flatMap {
            return@flatMap productService.create(it)
        }.flatMap {
            ServerResponse.created(URI.create("/api/v2/products/${it.id}"))
            .contentType(APPLICATION_JSON)
            .body(fromValue(it)) }
        .onErrorResume {
            val errorResponse: WebClientResponseException = it as WebClientResponseException
            if(errorResponse.statusCode == HttpStatus.BAD_REQUEST)
                return@onErrorResume ServerResponse.badRequest().contentType(APPLICATION_JSON).body(fromValue(errorResponse.responseBodyAsString))

            return@onErrorResume Mono.error(errorResponse)
        }
    }

    fun edit(request : ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        val product: Mono<Product> = request.bodyToMono(Product::class.java)

        return product.flatMap {
         productService.update(it, id)
        }.flatMap {
            ServerResponse.created(URI.create("/api/v2/products/${it.id}"))
                .contentType(APPLICATION_JSON)
                .body(fromValue(it))
        }.onErrorResume {
            val errorResponse: WebClientResponseException = it as WebClientResponseException
            if(errorResponse.statusCode == HttpStatus.NOT_FOUND)
                return@onErrorResume ServerResponse.notFound().build()

            return@onErrorResume Mono.error(errorResponse)
        }

    }

    fun delete(request : ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        val product: Mono<Product> = productService.findById(id)

        return product.flatMap {
            productService.delete(id).then(ServerResponse.noContent().build())
        }.switchIfEmpty(ServerResponse.notFound().build())
        .onErrorResume {
            val errorResponse: WebClientResponseException = it as WebClientResponseException
            if(errorResponse.statusCode == HttpStatus.NOT_FOUND)
                return@onErrorResume ServerResponse.notFound().build()

            return@onErrorResume Mono.error(errorResponse)
        }

    }

    fun upload(request : ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        return request.multipartData().map {
            it.toSingleValueMap().get("file")
        }.cast(FilePart::class.java)
        .flatMap { file ->
            productService.upload(file, id)
        }.flatMap {
            ServerResponse.created(URI.create("/api/client/${it.id}"))
                .contentType(APPLICATION_JSON)
                .body(fromValue(it))
        }.onErrorResume {
            val errorResponse: WebClientResponseException = it as WebClientResponseException
              if(errorResponse.statusCode == HttpStatus.NOT_FOUND)
                 return@onErrorResume ServerResponse.notFound().build()

            return@onErrorResume Mono.error(errorResponse)
        }
    }




}