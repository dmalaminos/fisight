package com.fisight.fisight

import com.fisight.fisight.account.AccountHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class AccountRouter {
    @Bean
    fun route(handler: AccountHandler) = router {
        "/accounts".nest {
            GET("/", handler::getAll)
        }
        GET("/status") { ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(fromObject("Up and running!")) }
    }
}