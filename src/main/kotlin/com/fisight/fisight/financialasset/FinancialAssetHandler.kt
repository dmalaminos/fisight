package com.fisight.fisight.financialasset

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class FinancialAssetHandler(private val financialAssetRepository: FinancialAssetRepository) {
    fun getAll(request: ServerRequest) =
            ServerResponse.ok().body(financialAssetRepository.findAll())

    fun getById(request: ServerRequest): Mono<ServerResponse> {
        val requestedId = request.pathVariable("id")
        return financialAssetRepository.findById(requestedId)
                .flatMap { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
                .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun save(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(FinancialAsset::class.java)
                .flatMap { financialAssetRepository.insert(it) }
                .flatMap { ServerResponse.created(UriComponentsBuilder.fromPath("/assets/{id}").buildAndExpand(it.id).toUri()).build() }
                .onErrorResume { ServerResponse.status(HttpStatus.CONFLICT).build() }
    }

    fun update(request: ServerRequest): Mono<ServerResponse> {
        val requestedId = request.pathVariable("id")
        return request.bodyToMono(FinancialAsset::class.java)
                .filter { it.id == requestedId }
                .flatMap { financialAssetRepository.save(it) }
                .flatMap { ServerResponse.ok().build() }
                .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun delete(request: ServerRequest): Mono<ServerResponse> {
        val requestedId = request.pathVariable("id")
        return financialAssetRepository.deleteById(requestedId)
                .flatMap { ServerResponse.ok().build() }
    }
}
