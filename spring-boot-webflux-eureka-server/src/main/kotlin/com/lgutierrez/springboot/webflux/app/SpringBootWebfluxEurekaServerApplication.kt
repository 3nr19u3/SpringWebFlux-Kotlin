package com.lgutierrez.springboot.webflux.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class SpringBootWebfluxEurekaServerApplication

fun main(args: Array<String>) {
    runApplication<SpringBootWebfluxEurekaServerApplication>(*args)
}
