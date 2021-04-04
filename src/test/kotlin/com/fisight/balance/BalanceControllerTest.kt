package com.fisight.balance

import com.fisight.money.Currency
import com.fisight.money.Money
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime

@WebMvcTest(BalanceController::class)
class BalanceControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var balanceService: BalanceService

    @Test
    fun `gets balance by location id at date`() {
        val dateOfBalance = LocalDateTime.of(2021, 4, 4, 13, 49)
        val balance = Balance(
            listOf(Money(25, Currency.EUR), Money(100, Currency.USD)),
            dateOfBalance
        )
        given(balanceService.calculateForLocation(42, dateOfBalance)).willReturn(balance)

        client.perform(MockMvcRequestBuilders.get("/balance/42?atDate=2021-04-04T13:49:00"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amounts[0].amount").value("25"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amounts[0].currency").value("EUR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amounts[1].amount").value("100"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amounts[1].currency").value("USD"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBalance").value("2021-04-04T13:49:00"))
    }
}