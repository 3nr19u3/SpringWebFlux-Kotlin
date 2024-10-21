package com.lgutierrez.springboot.webflux.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootWebfluxEurekaServerApplication

fun main(args: Array<String>) {
    runApplication<SpringBootWebfluxEurekaServerApplication>(*args)
}
