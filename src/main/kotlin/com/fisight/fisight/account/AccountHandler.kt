package com.fisight.fisight.account

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class AccountHandler(private val accountRepository: AccountRepository) {
    fun getAll(request: ServerRequest) =
            ServerResponse.ok().body(accountRepository.findAll())

    fun save(request: ServerRequest): Mono<ServerResponse> {
        val newAccount = request.bodyToMono(Account::class.java)
        //TODO: input validation
        //TODO: add host to created location builder
        return newAccount
                .flatMap { accountRepository.insert(it) }
                .flatMap { ServerResponse.created(UriComponentsBuilder.fromPath("/accounts/{id}").buildAndExpand(it.id).toUri()).build() }
                .onErrorResume { ServerResponse.status(HttpStatus.CONFLICT).build() }
    }

    fun update(request: ServerRequest): Mono<ServerResponse> {
        val requestedId = request.pathVariable("id")
        val updatedAccount = request.bodyToMono(Account::class.java)

        return updatedAccount
                .filter { it.id == requestedId }
                .flatMap { accountRepository.save(it) }
                .flatMap { ServerResponse.ok().build() }
                .switchIfEmpty(ServerResponse.badRequest().build())
    }

    fun delete(request: ServerRequest): Mono<ServerResponse> {
        val requestedId = request.pathVariable("id")

        return accountRepository.deleteById(requestedId)
                .flatMap { ServerResponse.ok().build() }
    }
}
