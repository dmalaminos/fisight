package com.fisight.fisight.account

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
        val savedAccount = newAccount.doOnNext { account -> accountRepository.save(account) }
        //TODO: add host to created location builder
        return savedAccount.flatMap { acc-> ServerResponse.created(UriComponentsBuilder.fromPath("/accounts/{id}").buildAndExpand(acc.id).toUri() ).build()}

    }
}
