package com.fisight.fisight.financialasset

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
class FinancialAssetTests {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var financialAssetRepository: FinancialAssetRepository

    @Test
    fun canGetFinancialAssets() {
        val assets = arrayOf(
                FinancialAsset("1", "My stocks", Capital(160)),
                FinancialAsset("2", "Shady asset", Capital(160)))
        given(financialAssetRepository.findAll()).willReturn(Flux.just(*assets))

        client.get()
                .uri("/assets")
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(FinancialAsset::class.java)
                .hasSize(2)
                .contains(*assets)
    }

    @Test
    fun canCreateFinancialAsset() {
        val asset = FinancialAsset("1", "My stocks", Capital(160))
        given(financialAssetRepository.insert(ArgumentMatchers.any<FinancialAsset>())).willReturn(Mono.just(asset))

        client.post()
                .uri("/assets")
                .body(BodyInserters.fromObject(asset))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("Location", Matchers.`is`("/assets/1"))
    }

    @Test
    fun cannotCreateFinancialAsset_whenIdAlreadyExists() {
        given(financialAssetRepository.insert(ArgumentMatchers.any<Mono<FinancialAsset>>())).willThrow(MongoWriteException::class.java)

        client.post()
                .uri("/assets")
                .body(BodyInserters.fromObject(FinancialAsset("1", "My stocks", Capital(160))))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun canUpdateFinancialAsset() {
        val asset = FinancialAsset("1", "My stocks", Capital(160))
        given(financialAssetRepository.findById("1")).willReturn(Mono.just(asset))
        given(financialAssetRepository.save(asset)).willReturn(Mono.just(asset))

        client.put()
                .uri("/assets/1")
                .body(BodyInserters.fromObject(asset))
                .exchange()
                .expectStatus().isOk
    }

    @Test
    fun cannotUpdateFinancialAsset_whenUrlIdDoesNotMatchBodyId() {
        val asset = FinancialAsset("1", "My stocks", Capital(160))
        given(financialAssetRepository.findById("1")).willReturn(Mono.just(asset))

        client.put()
                .uri("/assets/23")
                .body(BodyInserters.fromObject(asset))
                .exchange()
                .expectStatus().isBadRequest
    }

    @Test
    fun canDeleteFinancialAsset() {
        given(financialAssetRepository.deleteById("1")).willReturn(Mono.empty())

        client.delete()
                .uri("/assets/1")
                .exchange()
                .expectStatus().isOk
    }
}
