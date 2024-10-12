package com.lgutierrez.springboot.webflux.app

import com.lgutierrez.springboot.webflux.app.handler.ProductHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.RequestPredicates.*

@Configuration
class RouterFunctionConfig {

    @Bean
    fun routes(handler: ProductHandler): RouterFunction<ServerResponse> {
        return route(GET("/api/v2/products")) {
            return@route handler.list(it)
        }.andRoute(GET("/api/v2/products/{id}")){
            return@andRoute handler.show(it)
        }.andRoute(POST("/api/v2/products")){
            return@andRoute handler.create(it)
            //return@andRoute handler.crateCompleteProduct(it)
        }.andRoute(PUT("/api/v2/products/{id}")){
            return@andRoute handler.edit(it)
        }.andRoute(DELETE("/api/v2/products/{id}")){
            return@andRoute handler.delete(it)
        }.andRoute(POST("/api/v2/products/upload/{id}").or(POST("/api/v2/products/uploads/{id}"))){
            return@andRoute handler.upload(it)
        }
    }
}