package com.lgutierrez.springboot.webflux.app.controller

import com.lgutierrez.springboot.webflux.app.models.documents.Category
import com.lgutierrez.springboot.webflux.app.models.documents.Product
import com.lgutierrez.springboot.webflux.app.services.CategoryService
import com.lgutierrez.springboot.webflux.app.services.ProductService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID


@Controller
class ProductController {

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var categoryService: CategoryService

    @Value("\${config.uploads.path}")
    private lateinit var uploadsPath: String

    private val logger = LoggerFactory.getLogger(ProductController::class.java)

    @ModelAttribute("categories")
    fun categories(): Flux<Category>{
        return categoryService.findAll()
    }

    @GetMapping("/show/{id}")
    fun show(model: Model, @PathVariable id: String): Mono<String> {
        return productService.findById(id)
                             .doOnNext { p ->
                                model.addAttribute("product", p)
                                model.addAttribute("title", p.name)
                             }.switchIfEmpty(Mono.just(Product()))
                             .flatMap { p ->
                                if(p.id.isNullOrEmpty())
                                    return@flatMap Mono.error(InterruptedException("Product do not exist!"))

                                return@flatMap Mono.just(p)
                             }.then(Mono.just("show"))
                             .onErrorResume { ex -> Mono.just("redirect:/listar?error=product+dont+exit") }

    }

    @GetMapping("/uploads/img/{namePhoto:.+}")
    fun showImg(@PathVariable namePhoto: String): Mono<ResponseEntity<Resource>>{
        val path: Path = Paths.get(uploadsPath).resolve(namePhoto).toAbsolutePath()

        val img: Resource = UrlResource(path.toUri())

        return Mono.just(
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='${img.filename}'")
                .body(img)
        )
    }

    //modify the reactive stream
    @GetMapping("/list-data","/")
    fun list(model: Model): Mono<String> {

        val products: Flux<Product>  = productService.findAllWithUppercaseNames()
        products.subscribe {p -> logger.info(p.name)}

        model.addAttribute("products", products)
        model.addAttribute("title", "List of Products")

        return Mono.just("list")
    }

    @GetMapping("/form")
    fun create(model: Model): Mono<String> {
        model.addAttribute("product", Product())
        model.addAttribute("title", "Product Form")
        model.addAttribute("button","create")
        return Mono.just("form")
    }

    @PostMapping("/form")
    fun save(@Valid product: Product, bindingResult: BindingResult, model: Model, @RequestPart file: FilePart): Mono<String> {
        if(bindingResult.hasErrors()){
            model.addAttribute("title", "Errors in Product Form")
            model.addAttribute("button", "save")
            return Mono.just("form")
        }else{
            logger.error("No tiene errores en el form")
            logger.error("category ID -> ${product.category.id}")

            val category: Mono<Category> = categoryService.findById(product.category.id!!)

            return category.flatMap{ c ->

                if(file.filename().isNotEmpty()){
                    product.photo = (UUID.randomUUID().toString() + "-" + file.filename()
                                    .replace(" ", "")
                                    .replace(":","")
                                    .replace("\\",""))
                }

                product.category = c
                productService.save(product)
            }.doOnNext { p ->
                logger.info("Category: ${p.category.name} - category ID : ${p.category.id}")
                logger.info("Product: ${p.name} - saved correctly with ID : ${p.id}")
            }.flatMap { p ->
                if(file.filename().isNotEmpty()){
                    return@flatMap file.transferTo(File(uploadsPath+p.photo))
                }
                return@flatMap Mono.empty<Product>()
            }
            .thenReturn("redirect:/list-data")
        }
    }

    @GetMapping("/delete/{id}")
    fun delete(@PathVariable id: String):Mono<String>{
        return productService.findById(id)
                             .defaultIfEmpty(Product())
                             .flatMap { p ->
                                if(p.id == null) {
                                    return@flatMap Mono.error { throw InterruptedException("Product dont exist!") }
                                }
                                return@flatMap Mono.just(p)
                             }.flatMap { p ->
                                 logger.info("Deleting product ...${p.name}")
                                 logger.info("Deleting product with ID...${p.id}")
                                 //use the !! operator to assert value
                                return@flatMap productService.delete(p.id!!)
                             }.then(Mono.just("redirect:/?success=product+deleted+successfully"))
                             .onErrorResume { ex -> Mono.just("redirect:/?error=product+dont+exit") }
    }

    @GetMapping("/form/{id}")
    fun edit(@PathVariable id: String, model: Model): Mono<String> {
         val product: Mono<Product> = productService.findById(id)
                                                    .doOnNext { p -> logger.info("Product: ${p.name}") }
                                                    .defaultIfEmpty(Product())

        model.addAttribute("title","Edit Product")
        model.addAttribute("product",product)
        return Mono.just("form")
    }

    @GetMapping("/form-v2/{id}")
    fun editV2(@PathVariable id: String, model: Model): Mono<String> {
        return productService.findById(id)
            .doOnNext {
                p -> logger.info("Product: ${p.name}")
                model.addAttribute("title","Edit Product")
                model.addAttribute("product",p)
                model.addAttribute("button","edit")
            }
            .defaultIfEmpty(Product())
            .flatMap { p ->
                if (p.id == null){
                    return@flatMap Mono.error { throw InterruptedException("Algo salio mal!") }
                }
                return@flatMap Mono.just(p)
            }.then(Mono.just("form"))
            .onErrorResume { ex -> Mono.just("redirect:/list-data?error=productoinvalido!") }
    }

    //reactiveDataDriven mode to handler the backpressure
    @GetMapping("/list-datadriver")
    fun listDataDriver(model: Model): String{

        val products: Flux<Product>  = productService.findAll().map { product ->
            product.copy(name = product.name?.uppercase())
            //delay the retrieve of the data in 5 seconds
        }.delayElements(java.time.Duration.ofSeconds(2))

        products.subscribe {p->logger.info(p.name)}

        //insert into interface model object UI model, the attribute name and also wrap the products(flux stream)
        //and the number of elements into ReactiveDataDriverContext object
        model.addAttribute("products", ReactiveDataDriverContextVariable(products,2))
        model.addAttribute("title", "List of Products")

        return "list"
    }

    //full mode
    @GetMapping("/list-full")
    fun listFull(model: Model): String{

        val products: Flux<Product>  = productService.findAllWithUppercaseNamesRepeat(100)

        products.subscribe {p->logger.info(p.name)}

        //insert into interface model object UI model, the attribute name and also wrap the products(flux stream)
        //and the number of elements into ReactiveDataDriverContext object
        model.addAttribute("products", ReactiveDataDriverContextVariable(products,2))
        model.addAttribute("title", "List of Products")

        return "list"
    }

    //chunked mode to handler the backpressure
    @GetMapping("/list-chunked")
    fun listChunked(model: Model): String{

        val products: Flux<Product>  = productService.findAllWithUppercaseNamesRepeat(2000)

        products.subscribe {p->logger.info(p.name)}

        //insert into interface model object UI model, the attribute name and also wrap the products(flux stream)
        //and the number of elements into ReactiveDataDriverContext object
        model.addAttribute("products", ReactiveDataDriverContextVariable(products,2))
        model.addAttribute("title", "List of Products")

        return "list-chunked"
    }
}
