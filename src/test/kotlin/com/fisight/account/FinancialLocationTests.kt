package com.fisight.account

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(FinancialLocationController::class)
class FinancialLocationTests {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var financialLocationRepository: FinancialLocationRepository

    @Test
    fun shouldGetAllAccounts() {
        val accounts = arrayOf(
            FinancialLocation(1, "Main", "Bankster"),
            FinancialLocation(2, "Savings", "Altbank")
        )
        given(financialLocationRepository.findAll()).willReturn(accounts.toList())

        client.perform(get("/financial-locations/"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("Main"))
            .andExpect(jsonPath("$[0].entityName").value("Bankster"))
            .andExpect(jsonPath("$[1].name").value("Savings"))
            .andExpect(jsonPath("$[1].entityName").value("Altbank"))
    }

    @Test
    fun shouldGetAccountById() {
        val account = FinancialLocation(123, "Main", "Bankster")
        given(financialLocationRepository.findById(123)).willReturn(Optional.of(account))

        client.perform(get("/financial-locations/123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Main"))
            .andExpect(jsonPath("$.entityName").value("Bankster"))
    }

    @Test
    fun shouldNotGetAccountById_whenIdDoesNotExist() {
        given(financialLocationRepository.findById(123)).willReturn(Optional.empty())

        client.perform(get("/financial-locations/123"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun shouldCreateAccount() {
        client.perform(
            post("/financial-locations/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Main", "entityName":"Bankster"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/financial-locations/0"))
    }

    @Test
    fun shouldUpdateAccount() {
        val account = FinancialLocation(123, "Main", "Bankster")
        given(financialLocationRepository.findById(123)).willReturn(Optional.of(account))
        given(financialLocationRepository.save(account)).willReturn(account)

        client.perform(
            put("/financial-locations/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "123", "name": "Secondary", "entityName": "Bankster"}""")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun shouldNotUpdateAccount_whenUrlIdDoesNotMatchBodyId() {
        val account = FinancialLocation(123, "Main", "Bankster")
        given(financialLocationRepository.findById(123)).willReturn(Optional.of(account))

        client.perform(
            put("/financial-locations/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "234", "name": "Secondary", "entityName": "Bankster"}"""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun shouldDeleteAccount() {
        client.perform(delete("/financial-locations/1234"))
            .andExpect(status().isOk)
    }
}
