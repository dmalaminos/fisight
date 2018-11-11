package com.fisight.fisight;

import com.fisight.fisight.account.Account
import com.fisight.fisight.capital.Capital
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
                Account("1", "Main", "Bankster", Capital(3000)),
                Account("2", "Savings", "Altbank", Capital(7000)))
        accounts.forEach { mongoTemplate.save(it) }

        checkExpectedAccounts(accounts)
    }

    @Test
    fun cannotCreateAccount_whenIdAlreadyExists() {
        val firstAccount = Account("1", "Main", "Bankster", Capital(3000))
        mongoTemplate.save(firstAccount)

        val sameIdAccount = Account("1", "Savings", "Altbank", Capital(7000))
        client.post()
                .uri("/accounts")
                .body(BodyInserters.fromObject(sameIdAccount))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
        checkExpectedAccounts(arrayOf(firstAccount))
    }

    @Test
    fun canDeleteAccounts() {
        val accountToDelete = Account("1", "Main", "Bankster", Capital(3000))
        mongoTemplate.save(accountToDelete)

        client.delete()
                .uri("/accounts/1")
                .exchange()
                .expectStatus().isOk
        checkExpectedAccounts(emptyArray())

        client.delete()
                .uri("/accounts/1")
                .exchange()
                .expectStatus().isOk
        checkExpectedAccounts(emptyArray())

        client.delete()
                .uri("/accounts/1289")
                .exchange()
                .expectStatus().isOk
        checkExpectedAccounts(emptyArray())
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
