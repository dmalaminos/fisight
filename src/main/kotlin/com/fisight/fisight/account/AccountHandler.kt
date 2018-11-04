package com.fisight.fisight.account

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body

@Component
class AccountHandler(private val accountRepository: AccountRepository) {
    fun getAll(request: ServerRequest) =
            ServerResponse.ok().body(accountRepository.findAll())
}
