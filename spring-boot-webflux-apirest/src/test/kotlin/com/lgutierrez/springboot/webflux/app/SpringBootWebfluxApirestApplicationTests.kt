package com.lgutierrez.springboot.webflux.app

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.services.CategoryService
import com.lgutierrez.springboot.webflux.app.services.ProductService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.Collections

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringBootWebfluxApirestApplicationTests {

	@Autowired
	private lateinit var webTestClient: WebTestClient

	@Autowired
	private lateinit var productService: ProductService

	@Autowired
	private lateinit var categoryService: CategoryService

	@Value("\${config.base.uri}")
	private lateinit var baseUri: String

	@Test
	fun listTest() {
		webTestClient.get().uri(baseUri)
							.accept(MediaType.APPLICATION_JSON)
							.exchange()
							.expectStatus().isOk
							.expectHeader().contentType(MediaType.APPLICATION_JSON)
							.expectBodyList<Product>()
							.consumeWith<ListBodySpec<Product>> {
								val products: List<Product>? = it.responseBody
								assertThat(products!!.size == 13).isTrue()
							}
							//.hasSize(13)
	}

	@Test
	fun getTest() {
		val product: Product = productService.findByName("Monitor 32 4K").block()!!

		webTestClient.get().uri("$baseUri/{id}", Collections.singletonMap("id", product.id))
							.accept(MediaType.APPLICATION_JSON)
							.exchange()
							.expectStatus().isOk()
							.expectHeader().contentType(MediaType.APPLICATION_JSON)
							.expectBody()
							.jsonPath("$.id").isNotEmpty
							.jsonPath("$.name").isEqualTo("Monitor 32 4K")
							//.expectBody<Product>()
							//.consumeWith {
							//	assertThat(it.responseBody?.name).isEqualTo("Monitor 32 4K")
							//}
	}

	@Test
	fun createTest() {
		//get the category by name
		val category: Category = categoryService.findByName("Tool").block()!!
		//create a product
		val product = Product("e-book",120.00,category)

		webTestClient.post().uri(baseUri)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							//replace body method by bodyValue
							.bodyValue(product)
							//exchange should be called after receive the object from response
							.exchange()
							.expectStatus().isCreated()
							.expectHeader().contentType(MediaType.APPLICATION_JSON)
							.expectBody()
							.jsonPath("$.id").isNotEmpty
							.jsonPath("$.category.name").isEqualTo("Tool")
	}

	@Test
	fun editTest() {
		val product: Product = productService.findByName("Monitor 32 4K").block()!!
		val category: Category = categoryService.findByName("Tool").block()!!

		//create a product
		val productEdited = Product("Monitor 32 5K",120.00,category)

		webTestClient.put().uri("$baseUri/{id}", Collections.singletonMap("id", product.id))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.bodyValue(productEdited)
							.exchange()
							.expectStatus().isCreated()
							.expectHeader().contentType(MediaType.APPLICATION_JSON)
							.expectBody()
							.jsonPath("$.id").isNotEmpty
							.jsonPath("$.name").isEqualTo("Monitor 32 5K")
							.jsonPath("$.category.name").isEqualTo("Tool")
	}

	@Test
	fun deleteTest() {
		val product: Product = productService.findByName("Tarjeta Grafica XXXX").block()!!

		webTestClient.delete().uri("$baseUri/{id}", Collections.singletonMap("id", product.id))
							.exchange()
							.expectStatus().isNoContent()
							.expectBody()
							.isEmpty()


		webTestClient.get().uri("$baseUri/{id}", Collections.singletonMap("id", product.id))
							.accept(MediaType.APPLICATION_JSON)
							.exchange()
							.expectStatus().isNotFound()
							.expectBody()
							.isEmpty()

	}


}
