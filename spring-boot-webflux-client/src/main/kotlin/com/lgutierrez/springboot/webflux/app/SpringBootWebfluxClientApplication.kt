package com.lgutierrez.springboot.webflux.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootWebfluxClientApplication

fun main(args: Array<String>) {
	runApplication<SpringBootWebfluxClientApplication>(*args)
}
