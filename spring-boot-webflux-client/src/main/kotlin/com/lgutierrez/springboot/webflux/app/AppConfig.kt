package com.lgutierrez.springboot.webflux.app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class AppConfig {

    @Value("\${config.base.endpoint}")
    lateinit var baseUrl: String

    @Bean
    fun webClientRegister() :WebClient{
        return WebClient.create(baseUrl)
    }
}