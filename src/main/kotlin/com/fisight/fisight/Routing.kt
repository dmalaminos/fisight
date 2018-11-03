package com.fisight.fisight

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class Routing {
    @Bean
    fun route() = router {
        GET("/") { ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(fromObject("Hola!")) }
    }
}