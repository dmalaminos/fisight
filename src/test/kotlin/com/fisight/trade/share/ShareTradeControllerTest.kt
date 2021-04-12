package com.fisight.trade.share

import com.fisight.location.Location
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import com.fisight.trade.currency.TradeType
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


@WebMvcTest(ShareTradeController::class)
class ShareTradeControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var shareTradeService: ShareTradeService

    @MockBean
    private lateinit var mapper: ShareTradeMapper

    @Test
    fun `gets all share trades`() {
        val shareTrades = listOf(
            ShareTrade(
                1,
                "World ETF",
                "isin-code",
                "MyExchange",
                "ME:WETF",
                TradeType.Buy,
                Currency.EUR,
                Currency.EUR,
                Money(80.78, Currency.EUR),
                256.1,
                Money(5, Currency.EUR),
                LocalDateTime.of(2021, 4, 11, 22, 16),
                Location(4, "MyExchange", "Bestexchange", LocationType.Broker)
            ),
            ShareTrade(
                2,
                "World ETF",
                "isin-code",
                "MyExchange",
                "ME:WETF",
                TradeType.Sell,
                Currency.EUR,
                Currency.EUR,
                Money(85.78, Currency.EUR),
                64.2,
                Money(5, Currency.EUR),
                LocalDateTime.of(2021, 4, 11, 23, 56),
                Location(4, "MyExchange", "Bestexchange", LocationType.Broker)
            )
        )
        BDDMockito.given(shareTradeService.findAll()).willReturn(shareTrades)
        BDDMockito.given(mapper.toDto(shareTrades[0])).willCallRealMethod()
        BDDMockito.given(mapper.toDto(shareTrades[1])).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/trades/share/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                ResultMatcher.matchAll(
                    MockMvcResultMatchers.jsonPath("$[0].id").value(1),
                    MockMvcResultMatchers.jsonPath("$[0].name").value("World ETF"),
                    MockMvcResultMatchers.jsonPath("$[0].isinCode").value("isin-code"),
                    MockMvcResultMatchers.jsonPath("$[0].exchange").value("MyExchange"),
                    MockMvcResultMatchers.jsonPath("$[0].ticker").value("ME:WETF"),
                    MockMvcResultMatchers.jsonPath("$[0].tradeType").value("Buy"),
                    MockMvcResultMatchers.jsonPath("$[0].baseCurrency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[0].quoteCurrency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[0].pricePerBaseUnit.amount").value(80.78),
                    MockMvcResultMatchers.jsonPath("$[0].pricePerBaseUnit.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[0].quantity").value("256.1"),
                    MockMvcResultMatchers.jsonPath("$[0].fee.amount").value(5),
                    MockMvcResultMatchers.jsonPath("$[0].fee.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[0].dateTraded").value("2021-04-11T22:16:00"),
                    MockMvcResultMatchers.jsonPath("$[0].locationId").value(4),
                    MockMvcResultMatchers.jsonPath("$[1].id").value(2),
                    MockMvcResultMatchers.jsonPath("$[1].name").value("World ETF"),
                    MockMvcResultMatchers.jsonPath("$[1].isinCode").value("isin-code"),
                    MockMvcResultMatchers.jsonPath("$[1].exchange").value("MyExchange"),
                    MockMvcResultMatchers.jsonPath("$[1].ticker").value("ME:WETF"),
                    MockMvcResultMatchers.jsonPath("$[1].tradeType").value("Sell"),
                    MockMvcResultMatchers.jsonPath("$[1].baseCurrency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[1].quoteCurrency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[1].pricePerBaseUnit.amount").value(85.78),
                    MockMvcResultMatchers.jsonPath("$[1].pricePerBaseUnit.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[1].quantity").value("64.2"),
                    MockMvcResultMatchers.jsonPath("$[1].fee.amount").value(5),
                    MockMvcResultMatchers.jsonPath("$[1].fee.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$[1].dateTraded").value("2021-04-11T23:56:00"),
                    MockMvcResultMatchers.jsonPath("$[1].locationId").value(4)
                )
            )
    }

    @Test
    fun `gets a share trade by id`() {
        val shareTrade = ShareTrade(
            1,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            Location(4, "MyExchange", "Bestexchange", LocationType.Broker)
        )
        BDDMockito.given(shareTradeService.findById(1)).willReturn(Optional.of(shareTrade))
        BDDMockito.given(mapper.toDto(shareTrade)).willCallRealMethod()

        client.perform(MockMvcRequestBuilders.get("/trades/share/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                ResultMatcher.matchAll(
                    MockMvcResultMatchers.jsonPath("$.id").value(1),
                    MockMvcResultMatchers.jsonPath("$.name").value("World ETF"),
                    MockMvcResultMatchers.jsonPath("$.isinCode").value("isin-code"),
                    MockMvcResultMatchers.jsonPath("$.exchange").value("MyExchange"),
                    MockMvcResultMatchers.jsonPath("$.ticker").value("ME:WETF"),
                    MockMvcResultMatchers.jsonPath("$.tradeType").value("Buy"),
                    MockMvcResultMatchers.jsonPath("$.baseCurrency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.quoteCurrency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.pricePerBaseUnit.amount").value(80.78),
                    MockMvcResultMatchers.jsonPath("$.pricePerBaseUnit.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.quantity").value("256.1"),
                    MockMvcResultMatchers.jsonPath("$.fee.amount").value(5),
                    MockMvcResultMatchers.jsonPath("$.fee.currency").value("EUR"),
                    MockMvcResultMatchers.jsonPath("$.dateTraded").value("2021-04-11T22:16:00"),
                    MockMvcResultMatchers.jsonPath("$.locationId").value(4),
                )
            )
    }

    @Test
    fun `cannot get a share trade by id when id does not exist`() {
        BDDMockito.given(shareTradeService.findById(1)).willReturn(Optional.empty())

        client.perform(MockMvcRequestBuilders.get("/trades/share/1"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `creates a share trade`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val shareTrade = ShareTrade(
            1,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            location
        )
        val shareTradeDto = ShareTradeDto(
            null,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            location.id
        )
        BDDMockito.given(shareTradeService.save(shareTradeDto))
            .willReturn(shareTrade)

        client.perform(
            MockMvcRequestBuilders.post("/trades/share/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "name": "World ETF", 
                       "isinCode": "isin-code", 
                       "exchange": "MyExchange", 
                       "ticker": "ME:WETF", 
                       "tradeType": "Buy",
                       "baseCurrency": "EUR", 
                       "quoteCurrency": "EUR",
                       "pricePerBaseUnit": { "amount": 80.78, "currency": "EUR" },
                       "quantity": "256.1",
                       "fee": { "amount": 5, "currency": "EUR" },
                       "dateTraded": "2021-04-11T22:16:00",
                       "locationId": "5"}
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/trades/share/1"))
    }

    @Test
    fun `updates a share trade`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val shareTrade = ShareTrade(
            1,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            location
        )
        BDDMockito.given(shareTradeService.findById(1))
            .willReturn(Optional.of(shareTrade))

        client.perform(
            MockMvcRequestBuilders.put("/trades/share/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "1",  
                       "name": "World ETF", 
                       "isinCode": "isin-code", 
                       "exchange": "MyExchange", 
                       "ticker": "ME:WETF", 
                       "tradeType": "Buy",
                       "baseCurrency": "EUR", 
                       "quoteCurrency": "EUR",
                       "pricePerBaseUnit": { "amount": 81.78, "currency": "EUR" },
                       "quantity": "256.2",
                       "fee": { "amount": 5.12, "currency": "EUR" },
                       "dateTraded": "2021-04-11T22:16:00",
                       "locationId": "5"}
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `does not update a share trade when URL id does not match body id`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val shareTrade = ShareTrade(
            2,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            location
        )
        BDDMockito.given(shareTradeService.findById(1))
            .willReturn(Optional.of(shareTrade))

        client.perform(
            MockMvcRequestBuilders.put("/trades/share/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                     { "id": "22",  
                       "name": "World ETF", 
                       "isinCode": "isin-code", 
                       "exchange": "MyExchange", 
                       "ticker": "ME:WETF", 
                       "tradeType": "Buy",
                       "baseCurrency": "EUR", 
                       "quoteCurrency": "EUR",
                       "pricePerBaseUnit": { "amount": 81.78, "currency": "EUR" },
                       "quantity": "256.2",
                       "fee": { "amount": 5.12, "currency": "EUR" },
                       "dateTraded": "2021-04-11T22:16:00",
                       "locationId": "5" }
                     """.trimMargin()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `deletes a share trade`() {
        client.perform(MockMvcRequestBuilders.delete("/trades/share/1234"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}