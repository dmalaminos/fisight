package com.fisight.fisight.financialasset

import com.fisight.fisight.capital.Capital
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@RunWith(SpringRunner::class)
@WebMvcTest(FinancialAssetController::class)
class FinancialAssetTests {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var financialAssetRepository: FinancialAssetRepository

    @MockBean
    private lateinit var queryGateway: QueryGateway

    @MockBean
    private lateinit var commandGateway: CommandGateway

    @Test
    fun canGetFinancialAssets() {
        val assets = arrayOf(
                FinancialAsset("1", "My stocks", Capital(160.0)),
                FinancialAsset("2", "Shady asset", Capital(160.0)))
        given(financialAssetRepository.findAll()).willReturn(assets.toList())

        client.perform(MockMvcRequestBuilders.get("/assets/"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("My stocks"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].capital.amount").value("160.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Shady asset"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].capital.amount").value("160.0"))
    }

    @Test
    fun canGetFinancialAssetById() {
        val asset = FinancialAsset("123", "My stocks", Capital(160.0))
        given(financialAssetRepository.findById("123")).willReturn(Optional.of(asset))

        client.perform(MockMvcRequestBuilders.get("/assets/123"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("My stocks"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capital.amount").value("160.0"))
    }

    @Test
    fun cannotGetFinancialAssetById_whenIdDoesNotExist() {
        given(financialAssetRepository.findById("123")).willReturn(Optional.empty())

        client.perform(MockMvcRequestBuilders.get("/assets/123"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun canCreateFinancialAsset() {
        client.perform(MockMvcRequestBuilders.post("/assets/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("""{"id": "123", "name": "My stocks", "capital": {"amount": 3000.0}}"""))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.header().string("Location", "/assets/123"))
    }

    @Test
    fun canUpdateFinancialAsset() {
        val asset = FinancialAsset("123", "My stocks", Capital(160.0))
        given(financialAssetRepository.findById("123")).willReturn(Optional.of(asset))

        client.perform(MockMvcRequestBuilders.put("/assets/123")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("""{"id": "123", "name": "B-stocks", "capital": {"amount": 3000.0}}"""))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun cannotUpdateFinancialAsset_whenUrlIdDoesNotMatchBodyId() {
        val asset = FinancialAsset("123", "My stocks", Capital(160.0))
        given(financialAssetRepository.findById("123")).willReturn(Optional.of(asset))

        client.perform(MockMvcRequestBuilders.put("/assets/123")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("""{"id": "234", "name": "B-stocks", "capital": {"amount": 3000.0}}"""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun canDeleteFinancialAsset() {
        client.perform(MockMvcRequestBuilders.delete("/assets/1234"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
