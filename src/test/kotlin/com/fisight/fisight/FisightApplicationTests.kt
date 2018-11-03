package com.fisight.fisight

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureWebTestClient
class FisightApplicationTests {
    @Autowired
    private lateinit var client: WebTestClient

	@Test
	fun getHelloWorld() {
        client.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .returnResult().apply { assertEquals("Hola!", responseBody) }
    }
}
