package com.lgutierrez.springboot.webflux.app.services.impl

import com.lgutierrez.springboot.webflux.app.models.Product
import com.lgutierrez.springboot.webflux.app.services.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType.*
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProductServiceImpl: ProductService {

    @Autowired
    private lateinit var webClient: WebClient

    override fun findAll(): Flux<Product> {
        return webClient.get().accept(APPLICATION_JSON)
                              .exchangeToFlux {
                                    it.bodyToFlux(Product::class.java)
                              }
    }

    override fun findById(id: String): Mono<Product> {
        //fixed to be clean using $id instead of {/id}
        return webClient.get().uri("/$id")
                            .accept(APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(Product::class.java)
    }

    override fun create(product: Product): Mono<Product> {
        return webClient.post().accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(fromValue(product))
            .retrieve()
            .bodyToMono(Product::class.java)
    }

    override fun update(product: Product, id: String): Mono<Product> {
        return webClient.put().uri("/$id").accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(fromValue(product))
            .retrieve()
            .bodyToMono(Product::class.java)
    }

    override fun delete(id: String): Mono<Void> {
        return webClient.delete().uri("/$id")
            .exchangeToMono { null }
    }

    override fun upload(filePart: FilePart, id: String): Mono<Product> {
        val parts = MultipartBodyBuilder()
            parts.asyncPart("file", filePart.content(), DataBuffer::class.java).headers { headers ->
                headers.setContentDispositionFormData("file", filePart.filename())
            }

        return webClient.post()
                        .uri("/upload/$id")
                        .contentType(MULTIPART_FORM_DATA)
                        .body(fromValue(parts.build()))
                        .retrieve()
                        .bodyToMono(Product::class.java)

    }
}