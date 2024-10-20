package com.lgutierrez.springboot.webflux.app

import com.lgutierrez.springboot.webflux.app.handler.ProductHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class RouterConfig {

    @Bean
    fun routes(handler: ProductHandler): RouterFunction<ServerResponse> {
        return route(GET("/api/client")) {
            return@route handler.list(it)
        }.andRoute(GET("/api/client/{id}")){
            return@andRoute handler.show(it)
        }.andRoute(POST("/api/client")){
            return@andRoute handler.create(it)
        }.andRoute(PUT("/api/client/{id}")){
            return@andRoute handler.edit(it)
        }.andRoute(DELETE("/api/client/{id}")){
            return@andRoute handler.delete(it)
        }.andRoute(POST("/api/client/upload/{id}")){
            return@andRoute handler.upload(it)
        }
    }

}