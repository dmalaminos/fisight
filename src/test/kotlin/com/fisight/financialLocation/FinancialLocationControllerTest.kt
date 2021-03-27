package com.fisight.financialLocation

import java.util.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FinancialLocationController::class)
class FinancialLocationControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var financialLocationRepository: FinancialLocationRepository

    @Test
    fun `gets all financial locations`() {
        val financialLocations = arrayOf(
            FinancialLocation(1, "Main", "Bankster"),
            FinancialLocation(2, "Savings", "Altbank")
        )
        given(financialLocationRepository.findAll()).willReturn(financialLocations.toList())

        client.perform(get("/locations/"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("Main"))
            .andExpect(jsonPath("$[0].entityName").value("Bankster"))
            .andExpect(jsonPath("$[1].name").value("Savings"))
            .andExpect(jsonPath("$[1].entityName").value("Altbank"))
    }

    @Test
    fun `gets a financial location by id`() {
        val financialLocation = FinancialLocation(123, "Main", "Bankster")
        given(financialLocationRepository.findById(123)).willReturn(Optional.of(financialLocation))

        client.perform(get("/locations/123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Main"))
            .andExpect(jsonPath("$.entityName").value("Bankster"))
    }

    @Test
    fun `cannot get a financial location by id when id does not exist`() {
        given(financialLocationRepository.findById(123)).willReturn(Optional.empty())

        client.perform(get("/locations/123"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `creates a financial location`() {
        client.perform(
            post("/locations/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Main", "entityName":"Bankster"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/locations/0"))
    }

    @Test
    fun `updates a financial location`() {
        val financialLocation = FinancialLocation(123, "Main", "Bankster")
        given(financialLocationRepository.findById(123)).willReturn(Optional.of(financialLocation))
        given(financialLocationRepository.save(financialLocation)).willReturn(financialLocation)

        client.perform(
            put("/locations/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "123", "name": "Secondary", "entityName": "Bankster"}""")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `does not update a financial location when URL id does not match body id`() {
        val financialLocation = FinancialLocation(123, "Main", "Bankster")
        given(financialLocationRepository.findById(123)).willReturn(Optional.of(financialLocation))

        client.perform(
            put("/locations/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "234", "name": "Secondary", "entityName": "Bankster"}"""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deletes a financial location`() {
        client.perform(delete("/locations/1234"))
            .andExpect(status().isOk)
    }
}
