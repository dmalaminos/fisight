package com.fisight.location

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

@WebMvcTest(LocationController::class)
class LocationControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var locationService: LocationService

    @Test
    fun `gets all locations`() {
        val locations = arrayOf(
            Location(1, "Main", "Bankster"),
            Location(2, "Savings", "Altbank")
        )
        given(locationService.findAll()).willReturn(locations.toList())

        client.perform(get("/locations/"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("Main"))
            .andExpect(jsonPath("$[0].entityName").value("Bankster"))
            .andExpect(jsonPath("$[1].name").value("Savings"))
            .andExpect(jsonPath("$[1].entityName").value("Altbank"))
    }

    @Test
    fun `gets a location by id`() {
        val location = Location(123, "Main", "Bankster")
        given(locationService.findById(123)).willReturn(Optional.of(location))

        client.perform(get("/locations/123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Main"))
            .andExpect(jsonPath("$.entityName").value("Bankster"))
    }

    @Test
    fun `cannot get a location by id when id does not exist`() {
        given(locationService.findById(123)).willReturn(Optional.empty())

        client.perform(get("/locations/123"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `creates a location`() {
        val location = Location(0, "Main", "Bankster")
        given(locationService.save(location)).willReturn(location.copy(id = 1))

        client.perform(
            post("/locations/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Main", "entityName":"Bankster"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/locations/1"))
    }

    @Test
    fun `updates a location`() {
        val location = Location(123, "Main", "Bankster")
        given(locationService.findById(123)).willReturn(Optional.of(location))
        given(locationService.save(location)).willReturn(location)

        client.perform(
            put("/locations/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "123", "name": "Secondary", "entityName": "Bankster"}""")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `does not update a location when URL id does not match body id`() {
        val location = Location(123, "Main", "Bankster")
        given(locationService.findById(123)).willReturn(Optional.of(location))

        client.perform(
            put("/locations/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "234", "name": "Secondary", "entityName": "Bankster"}"""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deletes a location`() {
        client.perform(delete("/locations/1234"))
            .andExpect(status().isOk)
    }
}
