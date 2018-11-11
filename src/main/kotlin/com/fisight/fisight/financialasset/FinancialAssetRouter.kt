package com.fisight.fisight.financialasset

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class FinancialAssetRouter {
    @Bean
    fun financialAssetRoutes(handler: FinancialAssetHandler) = router {
        "/assets".nest {
            GET("", handler::getAll)
            POST("", handler::save)
            PUT("/{id}", handler::update)
            DELETE("/{id}", handler::delete)
        }
    }
}