package com.fisight.fisight.account

import com.mongodb.MongoWriteException
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureWebTestClient
class AccountTests {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var accountRepository: AccountRepository

    @Test
    fun canGetExistingAccounts() {
        val accounts = arrayOf(
                Account("1", "Main", "Bankster", 3000),
                Account("2", "Savings", "Altbank", 7000))
        given(accountRepository.findAll()).willReturn(Flux.just(*accounts))

        client.get()
                .uri("/accounts")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Account::class.java)
                .hasSize(2)
                .contains(*accounts)
    }

    @Test
    fun canCreateAccount() {
        val account = Account("1234", "Main", "Bankster", 3000)
        given(accountRepository.insert(ArgumentMatchers.any<Account>())).willReturn(Mono.just(account))

        client.post()
                .uri("/accounts")
                .body(BodyInserters.fromObject(account))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("Location", Matchers.`is`("/accounts/1234"))
    }

    @Test
    fun cannotCreateAccount_whenIdAlreadyExists() {
        given(accountRepository.insert(ArgumentMatchers.any<Mono<Account>>())).willThrow(MongoWriteException::class.java)

        client.post()
                .uri("/accounts")
                .body(BodyInserters.fromObject(Account("1234", "Main", "Bankster", 3000)))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun canUpdateAccount() {
        val account = Account("1234", "Main", "Bankster", 3000)
        given(accountRepository.findById("1234")).willReturn(Mono.just(account))
        given(accountRepository.save(account)).willReturn(Mono.just(account))

        client.put()
                .uri("/accounts/1234")
                .body(BodyInserters.fromObject(account))
                .exchange()
                .expectStatus().isOk
    }

    @Test
    fun cannotUpdateAccount_whenUrlIdDoesNotMatchBodyId() {
        val account = Account("1234", "Main", "Bankster", 3000)
        given(accountRepository.findById("123")).willReturn(Mono.just(account))

        client.put()
                .uri("/accounts/123")
                .body(BodyInserters.fromObject(account))
                .exchange()
                .expectStatus().isBadRequest
    }

    @Test
    fun canDeleteAccount() {
        val account = Account("1234", "Main", "Bankster", 3000)
        given(accountRepository.deleteById("1234")).willReturn(Mono.empty())

        client.delete()
                .uri("/accounts/1234")
                .exchange()
                .expectStatus().isOk
    }
}