package com.fisight.transfer

import com.fisight.location.Location
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import java.util.Optional
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(TransferController::class)
class TransferControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var transferService: TransferService

    @MockBean
    private lateinit var mapper: TransferMapper

    @Test
    fun `gets all transfers`() {
        val transfers = listOf(
            Transfer(
                1,
                Location(5, "Main", "Bankster", LocationType.BankAccount),
                Location(8, "Secondary", "NuBank", LocationType.BankAccount),
                Money(100, Currency.EUR),
                Money(1, Currency.EUR),
                LocalDateTime.of(2021, 4, 2, 17, 21)
            ),
            Transfer(
                3,
                Location(8, "Secondary", "NuBank", LocationType.BankAccount),
                Location(5, "Main", "Bankster", LocationType.BankAccount),
                Money(5, Currency.EUR),
                Money(0, Currency.EUR),
                LocalDateTime.of(2021, 4, 2, 20, 21)
            )
        )
        BDDMockito.given(transferService.findAll()).willReturn(transfers)
        BDDMockito.given(mapper.toDto(transfers[0])).willCallRealMethod()
        BDDMockito.given(mapper.toDto(transfers[1])).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/transfers/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].sourceLocationId").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].targetLocationId").value("8"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount.amount").value("100"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount.currency").value("EUR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].fee.amount").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].fee.currency").value("EUR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].dateTransferred").value("2021-04-02T17:21:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].sourceLocationId").value("8"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].targetLocationId").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount.amount").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount.currency").value("EUR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].fee.amount").value("0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].fee.currency").value("EUR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].dateTransferred").value("2021-04-02T20:21:00"))
    }

    @Test
    fun `gets a transfer by id`() {
        val transfer = Transfer(
            21,
            Location(5, "Main", "Bankster", LocationType.BankAccount),
            Location(8, "Secondary", "NuBank", LocationType.BankAccount),
            Money(100, Currency.EUR),
            Money(1, Currency.EUR),
            LocalDateTime.of(2021, 4, 2, 17, 21)
        )
        BDDMockito.given(transferService.findById(21)).willReturn(Optional.of(transfer))
        BDDMockito.given(mapper.toDto(transfer)).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/transfers/21"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                ResultMatcher.matchAll(
                    MockMvcResultMatchers.jsonPath("$.id").value("21"),
                    MockMvcResultMatchers.jsonPath("$.sourceLocationId").value("5"),
                    MockMvcResultMatchers.jsonPath("$.targetLocationId").value("8"),
                    MockMvcResultMatchers.jsonPath("$.amount.amount").value("100"),
                    MockMvcResultMatchers.jsonPath("$.amount.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.fee.amount").value("1"),
                    MockMvcResultMatchers.jsonPath("$.fee.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.dateTransferred").value("2021-04-02T17:21:00")
                )
            )
    }

    @Test
    fun `cannot get a transfer by id when id does not exist`() {
        BDDMockito.given(transferService.findById(90)).willReturn(Optional.empty())

        client.perform(MockMvcRequestBuilders.get("/transfers/21"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `creates a transfer`() {
        val transfer = Transfer(
            22,
            Location(5, "Main", "Bankster", LocationType.BankAccount),
            Location(8, "Secondary", "NuBank", LocationType.BankAccount),
            Money(100, Currency.EUR),
            Money(1, Currency.EUR),
            LocalDateTime.of(2021, 4, 2, 17, 21)
        )
        BDDMockito.given(transferService.save(any()))
            .willReturn(transfer)

        client.perform(
            MockMvcRequestBuilders.post("/transfers/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "sourceLocationId": "5",
                       "targetLocationId": "8",
                       "amount": { "amount": 100, "currency": "EUR" },
                       "fee": { "amount": 1, "currency": "EUR" },
                       "dateTransferred": "2021-04-02T17:21:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/transfers/22"))
    }

    @Test
    fun `updates a transfer`() {
        val transfer = Transfer(
            22,
            Location(5, "Main", "Bankster", LocationType.BankAccount),
            Location(8, "Secondary", "NuBank", LocationType.BankAccount),
            Money(100, Currency.EUR),
            Money(1, Currency.EUR),
            LocalDateTime.of(2021, 4, 2, 17, 21)
        )
        BDDMockito.given(transferService.findById(22))
            .willReturn(Optional.of(transfer))

        client.perform(
            MockMvcRequestBuilders.put("/transfers/22")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "22",
                       "sourceLocationId": "5",
                       "targetLocationId": "7",
                       "amount": { "amount": 10, "currency": "EUR" },
                       "fee": { "amount": 0.5, "currency": "EUR" },
                       "dateTransferred": "2021-04-02T18:21:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `does not update a transfer when URL id does not match body id`() {
        client.perform(
            MockMvcRequestBuilders.put("/transfers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "42",
                       "sourceLocationId": "4",
                       "targetLocationId": "7",
                       "amount": { "amount": 10, "currency": "EUR" },
                       "fee": { "amount": 0, "currency": "EUR" },
                       "dateTransferred": "2021-04-02T18:21:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `deletes a transfer`() {
        client.perform(MockMvcRequestBuilders.delete("/transfers/1234"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}