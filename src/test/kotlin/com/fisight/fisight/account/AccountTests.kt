package com.fisight.fisight.account

import com.fisight.fisight.capital.Capital
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
    fun canGetAccounts() {
        val accounts = arrayOf(
                Account("1", "Main", "Bankster", Capital(3000)),
                Account("2", "Savings", "Altbank", Capital(7000)))
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
    fun canGetAccountById() {
        val account = Account("123", "Main", "Bankster", Capital(3000))

        given(accountRepository.findById("123")).willReturn(Mono.just(account))

        client.get()
                .uri("/accounts/123")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Account::class.java)
                .hasSize(1)
                .contains(account)
    }

    @Test
    fun cannotGetAccountById_whenIdDoesNotExist() {
        given(accountRepository.findById("123")).willReturn(Mono.empty())

        client.get()
                .uri("/accounts/123")
                .exchange()
                .expectStatus().isBadRequest
    }

    @Test
    fun canCreateAccount() {
        val account = Account("1234", "Main", "Bankster", Capital(3000))

        client.post()
                .uri("/accounts")
                .body(BodyInserters.fromObject(account))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("Location", Matchers.startsWith("/accounts/"))
    }

    @Test
    fun canUpdateAccount() {
        val account = Account("1234", "Main", "Bankster", Capital(3000))
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
        val account = Account("1234", "Main", "Bankster", Capital(3000))
        given(accountRepository.findById("123")).willReturn(Mono.just(account))

        client.put()
                .uri("/accounts/123")
                .body(BodyInserters.fromObject(account))
                .exchange()
                .expectStatus().isBadRequest
    }

    @Test
    fun canDeleteAccount() {
        given(accountRepository.deleteById("1234")).willReturn(Mono.empty())

        client.delete()
                .uri("/accounts/1234")
                .exchange()
                .expectStatus().isOk
    }
}
