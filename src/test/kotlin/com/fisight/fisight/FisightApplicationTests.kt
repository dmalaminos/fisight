package com.fisight.fisight

import com.fisight.fisight.account.Account
import com.fisight.fisight.account.AccountRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureWebTestClient
class FisightApplicationTests {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var accountRepository: AccountRepository

	@Test
	fun getStatus() {
        client.get()
                .uri("/status")
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .returnResult().apply { assertEquals("Up and running!", responseBody) }
    }

    @Test
    fun getAccounts() {
        val accounts = arrayOf(
                Account("1", "Main", "Bankster", 3000),
                Account("2", "Savings", "Altbank", 7000))
        given(accountRepository.findAll()).willReturn(Flux.just(*accounts))

        client.get()
                .uri("/accounts/")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Account::class.java)
                .hasSize(2)
                .contains(*accounts)
    }
}
