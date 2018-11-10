package com.fisight.fisight;

import com.fisight.fisight.account.Account
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

//@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureWebTestClient
class AccountIT {
    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun setUp() = mongoTemplate.dropCollection(Account::class.java)

    @AfterEach
    fun tearDown() = mongoTemplate.dropCollection(Account::class.java)

    @Test
    fun canRetrieveCreatedAccount() {
        checkExpectedAccounts(emptyArray())

        val accounts = arrayOf(
                Account("1", "Main", "Bankster", 3000),
                Account("2", "Savings", "Altbank", 7000))
        accounts.forEach {
            client.post()
                    .uri("/accounts")
                    .body(BodyInserters.fromObject(it))
                    .exchange()
                    .expectStatus().isCreated
                    .expectHeader().value("Location", Matchers.`is`("/accounts/${it.id}"))
        }

        checkExpectedAccounts(accounts)
    }

    @Test
    fun cannotCreateAccount_whenIdAlreadyExists() {
        val firstAccount = Account("1", "Main", "Bankster", 3000)
        val sameIdAccount = Account("1", "Savings", "Altbank", 7000)

        client.post()
                .uri("/accounts")
                .body(BodyInserters.fromObject(firstAccount))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("Location", Matchers.`is`("/accounts/${firstAccount.id}"))

        client.post()
                .uri("/accounts")
                .body(BodyInserters.fromObject(sameIdAccount))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)

        checkExpectedAccounts(arrayOf(firstAccount))
    }

    private fun checkExpectedAccounts(accounts: Array<Account>) {
        client.get()
                .uri("/accounts")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Account::class.java)
                .hasSize(accounts.size)
                .contains(*accounts)
    }
}
