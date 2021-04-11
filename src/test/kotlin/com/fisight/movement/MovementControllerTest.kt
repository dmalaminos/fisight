package com.fisight.movement

import com.fisight.location.Location
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import java.util.Optional
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(MovementController::class)
class MovementControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var movementService: MovementService

    @MockBean
    private lateinit var mapper: MovementMapper

    @Test
    fun `gets all movements for location`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movements = listOf(
            Movement(
                1,
                Money(300, Currency.EUR),
                MovementDirection.Inbound,
                location,
                MovementType.Wage,
                LocalDateTime.of(2021, 4, 8, 23, 56)
            ),
            Movement(
                2,
                Money(5, Currency.EUR),
                MovementDirection.Outbound,
                location,
                MovementType.Expense,
                LocalDateTime.of(2021, 4, 8, 22, 23)
            )
        )
        BDDMockito.given(movementService.findAllByLocation(5)).willReturn(movements)
        BDDMockito.given(mapper.toDto(movements[0])).willCallRealMethod()
        BDDMockito.given(mapper.toDto(movements[1])).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/locations/5/movements/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount.amount").value("300"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount.currency").value("EUR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].direction").value("Inbound"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].locationId").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].type").value("Wage"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].dateMoved").value("2021-04-08T23:56:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount.amount").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount.currency").value("EUR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].direction").value("Outbound"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].locationId").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].type").value("Expense"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].dateMoved").value("2021-04-08T22:23:00"))
    }

    @Test
    fun `gets a movement by id`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movement = Movement(
            12,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )
        BDDMockito.given(movementService.findById(12)).willReturn(Optional.of(movement))
        BDDMockito.given(mapper.toDto(movement)).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/movements/12"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                ResultMatcher.matchAll(
                    MockMvcResultMatchers.jsonPath("$.id").value("12"),
                    MockMvcResultMatchers.jsonPath("$.amount.amount").value("300"),
                    MockMvcResultMatchers.jsonPath("$.amount.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.direction").value("Inbound"),
                    MockMvcResultMatchers.jsonPath("$.locationId").value("5"),
                    MockMvcResultMatchers.jsonPath("$.type").value("Wage"),
                    MockMvcResultMatchers.jsonPath("$.dateMoved").value("2021-04-08T23:56:00")
                )
            )
    }

    @Test
    fun `cannot get a movement by id when id does not exist`() {
        BDDMockito.given(movementService.findById(90)).willReturn(Optional.empty())

        client.perform(MockMvcRequestBuilders.get("/movements/12"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `creates a movement`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movement = Movement(
            2,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )
        val movementDto = MovementDto(
            null,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location.id,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )
        BDDMockito.given(movementService.save(movementDto))
            .willReturn(movement)

        client.perform(
            MockMvcRequestBuilders.post("/movements/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "locationId": "5",
                       "direction": "Inbound",
                       "type": "Wage",
                       "amount": { "amount": 300, "currency": "EUR" },
                       "dateMoved": "2021-04-08T23:56:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/movements/2"))
    }

    @Test
    fun `updates a movement`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movement = Movement(
            12,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )
        BDDMockito.given(movementService.findById(12))
            .willReturn(Optional.of(movement))

        client.perform(
            MockMvcRequestBuilders.put("/movements/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "12",
                       "locationId": "5",
                       "direction": "Inbound",
                       "type": "Wage",
                       "amount": { "amount": 300, "currency": "EUR" },
                       "dateMoved": "2021-04-08T23:56:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `does not update a movement when URL id does not match body id`() {
        client.perform(
            MockMvcRequestBuilders.put("/movements/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "12",
                       "locationId": "5",
                       "direction": "Inbound",
                       "type": "Wage",
                       "amount": { "amount": 300, "currency": "EUR" },
                       "dateMoved": "2021-04-08T23:56:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `deletes a movement`() {
        client.perform(MockMvcRequestBuilders.delete("/movements/1234"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}