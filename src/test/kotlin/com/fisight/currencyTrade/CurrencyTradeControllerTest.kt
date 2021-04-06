package com.fisight.currencyTrade

import com.fisight.location.Location
import com.fisight.money.Currency
import com.fisight.money.Money
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher.matchAll
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(CurrencyTradeController::class)
class CurrencyTradeControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var currencyTradeService: CurrencyTradeService

    @MockBean
    private lateinit var mapper: CurrencyTradeMapper

    @Test
    fun `gets all currency trades`() {
        val currencyTrades = listOf(
            CurrencyTrade(
                1,
                Currency.EUR,
                Currency.BTC,
                CurrencyTradeType.Buy,
                Money(1000, Currency.EUR),
                0.007,
                Money(3, Currency.EUR),
                LocalDateTime.of(2021, 3, 24, 23, 44),
                Location(4, "MyExchange", "Bestexchange")
            ),
            CurrencyTrade(
                2,
                Currency.EUR,
                Currency.BTC,
                CurrencyTradeType.Buy,
                Money(200, Currency.EUR),
                0.007,
                Money(3, Currency.EUR),
                LocalDateTime.of(2021, 3, 24, 23, 50),
                Location(4, "MyExchange", "Bestexchange")
            )
        )
        BDDMockito.given(currencyTradeService.findAll()).willReturn(currencyTrades)
        BDDMockito.given(mapper.toDto(currencyTrades[0])).willCallRealMethod()
        BDDMockito.given(mapper.toDto(currencyTrades[1])).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/currency-trades/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                matchCurrencyTrade(
                    1, "EUR", "BTC", "Buy", 1000, "EUR", "0.007",
                    3, "EUR", "2021-03-24T23:44:00", 4, "MyExchange", "Bestexchange"
                )
            )
    }

    @Test
    fun `gets a currency trade by id`() {
        val currencyTrade = CurrencyTrade(
            1,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            Location(4, "MyExchange", "Bestexchange")
        )
        BDDMockito.given(currencyTradeService.findById(1)).willReturn(Optional.of(currencyTrade))
        BDDMockito.given(mapper.toDto(currencyTrade)).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/currency-trades/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                matchAll(
                    MockMvcResultMatchers.jsonPath("$.id").value(1),
                    MockMvcResultMatchers.jsonPath("$.baseCurrency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.quoteCurrency").value("BTC"),
                    MockMvcResultMatchers.jsonPath("$.tradeType").value("Buy"),
                    MockMvcResultMatchers.jsonPath("$.pricePerBaseUnit.amount").value(1000),
                    MockMvcResultMatchers.jsonPath("$.pricePerBaseUnit.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.quantity").value("0.007"),
                    MockMvcResultMatchers.jsonPath("$.fee.amount").value(3),
                    MockMvcResultMatchers.jsonPath("$.fee.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.dateTraded").value("2021-03-24T23:44:00"),
                    MockMvcResultMatchers.jsonPath("$.location.id").value(4),
                    MockMvcResultMatchers.jsonPath("$.location.name").value("MyExchange"),
                    MockMvcResultMatchers.jsonPath("$.location.entityName").value("Bestexchange")
                )
            )
    }

    @Test
    fun `cannot get a currency trade by id when id does not exist`() {
        BDDMockito.given(currencyTradeService.findById(1)).willReturn(Optional.empty())

        client.perform(MockMvcRequestBuilders.get("/currency-trades/1"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `creates a currency trade`() {
        val location = Location(5, "Main", "Bankster")
        val currencyTrade = CurrencyTrade(
            1,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            location
        )
        BDDMockito.given(currencyTradeService.save(any(), eq(5)))
            .willReturn(currencyTrade)

        client.perform(
            MockMvcRequestBuilders.post("/locations/5/currency-trades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "baseCurrency": "EUR", 
                       "quoteCurrency": "BTC",
                       "tradeType": "Sell",
                       "pricePerBaseUnit": { "amount": 20, "currency": "EUR" },
                       "quantity": "1",
                       "fee": { "amount": 0.2, "currency": "EUR" },
                       "dateTraded": "2021-03-24T23:44:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/currency-trades/1"))
    }

    @Test
    fun `updates a currency trade`() {
        val location = Location(5, "Main", "Bankster")
        val currencyTrade = CurrencyTrade(
            1,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            location
        )
        BDDMockito.given(currencyTradeService.findById(1))
            .willReturn(Optional.of(currencyTrade))

        client.perform(
            MockMvcRequestBuilders.put("/locations/5/currency-trades/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "1", 
                       "baseCurrency": "EUR", 
                       "quoteCurrency": "LTC",
                       "tradeType": "Sell",
                       "pricePerBaseUnit": { "amount": 20, "currency": "EUR" },
                       "quantity": "1",
                       "fee": { "amount": 0.2, "currency": "EUR" },
                       "dateTraded": "2021-03-29T20:24:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `does not update a currency trade when URL id does not match body id`() {
        val location = Location(5, "Main", "Bankster")
        val currencyTrade = CurrencyTrade(
            2,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            location
        )
        BDDMockito.given(currencyTradeService.findById(1))
            .willReturn(Optional.of(currencyTrade))

        client.perform(
            MockMvcRequestBuilders.put("/locations/5/currency-trades/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "123", 
                       "baseCurrency": "EUR", 
                       "quoteCurrency": "LTC",
                       "tradeType": "Sell",
                       "pricePerBaseUnit": { "amount": 20, "currency": "EUR" },
                       "quantity": "1",
                       "fee": { "amount": 0.2, "currency": "EUR" },
                       "dateTraded": "2021-03-29T20:24:00" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `deletes a currency trade`() {
        client.perform(MockMvcRequestBuilders.delete("/currency-trades/1234"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    private fun matchCurrencyTrade(
        id: Int,
        baseCurrency: String,
        quoteCurrency: String,
        tradeType: String,
        pricePerBaseUnitAmount: Int,
        pricePerBaseUnitCurrency: String,
        quantity: String,
        feeAmount: Int,
        feeCurrency: String,
        dateTraded: String,
        locationId: Int,
        locationName: String,
        locationEntityName: String,
        position: Int = 0
    ) = matchAll(
        MockMvcResultMatchers.jsonPath("$[$position].id").value(id),
        MockMvcResultMatchers.jsonPath("$[$position].baseCurrency").value(baseCurrency),
        MockMvcResultMatchers.jsonPath("$[$position].quoteCurrency").value(quoteCurrency),
        MockMvcResultMatchers.jsonPath("$[$position].tradeType").value(tradeType),
        MockMvcResultMatchers.jsonPath("$[$position].pricePerBaseUnit.amount").value(pricePerBaseUnitAmount),
        MockMvcResultMatchers.jsonPath("$[$position].pricePerBaseUnit.currency").value(pricePerBaseUnitCurrency),
        MockMvcResultMatchers.jsonPath("$[$position].quantity").value(quantity),
        MockMvcResultMatchers.jsonPath("$[$position].fee.amount").value(feeAmount),
        MockMvcResultMatchers.jsonPath("$[$position].fee.currency").value(feeCurrency),
        MockMvcResultMatchers.jsonPath("$[$position].dateTraded").value(dateTraded),
        MockMvcResultMatchers.jsonPath("$[$position].location.id").value(locationId),
        MockMvcResultMatchers.jsonPath("$[$position].location.name").value(locationName),
        MockMvcResultMatchers.jsonPath("$[$position].location.entityName").value(locationEntityName)
    )
}
