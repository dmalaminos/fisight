package com.fisight.comment

import com.fisight.location.Location
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import com.fisight.trade.currency.CurrencyTrade
import com.fisight.trade.currency.TradeType
import com.fisight.transfer.Transfer
import java.time.LocalDateTime
import java.util.Optional
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(CommentController::class)
class CommentControllerTest {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var commentService: CommentService

    @Test
    fun `gets all comments for location`() {
        val location = Location(1, "Main", "Bankster", LocationType.BankAccount)
        val comments = listOf(
            Comment(11, "Something", location),
            Comment(12, "More text", location),
        )
        BDDMockito.given(commentService.findAllForLocation(1)).willReturn(comments)

        client.perform(MockMvcRequestBuilders.get("/locations/1/comments/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("Something"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].text").value("More text"))
    }

    @Test
    fun `gets all comments for transfer`() {
        val sourceLocation = Location(42, "Main", "Bankster", LocationType.BankAccount)
        val targetLocation = Location(21, "Secondary", "NuBank", LocationType.BankAccount)
        val transfer = Transfer(
            11,
            sourceLocation,
            targetLocation,
            Money(4, Currency.EUR),
            Money.zero(Currency.EUR),
            LocalDateTime.of(2021, 4, 5, 23, 5)
        )
        val comments = listOf(
            Comment(11, "Something", transfer),
            Comment(12, "More text", transfer),
        )
        BDDMockito.given(commentService.findAllForTransfer(11)).willReturn(comments)

        client.perform(MockMvcRequestBuilders.get("/transfers/11/comments/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("Something"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].text").value("More text"))
    }

    @Test
    fun `gets all comments for currency trade`() {
        val location = Location(42, "Main", "Bankster", LocationType.BankAccount)
        val currencyTrade = CurrencyTrade(
            11,
            Currency.BTC,
            Currency.EUR,
            TradeType.Buy,
            Money(47000, Currency.EUR),
            0.0023,
            Money(1, Currency.EUR),
            LocalDateTime.of(2021, 4, 5, 23, 5),
            location
        )
        val comments = listOf(
            Comment(11, "Something", currencyTrade),
            Comment(12, "More text", currencyTrade)
        )
        BDDMockito.given(commentService.findAllForCurrencyTrade(11)).willReturn(comments)

        client.perform(MockMvcRequestBuilders.get("/currency-trades/11/comments/"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("Something"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].text").value("More text"))
    }

    @Test
    fun `gets a comment by id`() {
        val location = Location(33, "Main", "Bankster", LocationType.BankAccount)
        val comment = Comment(12, "Something", location)
        BDDMockito.given(commentService.findById(12)).willReturn(Optional.of(comment))

        client.perform(MockMvcRequestBuilders.get("/comments/12"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("Something"))
    }

    @Test
    fun `cannot get a comment by id when id does not exist`() {
        BDDMockito.given(commentService.findById(99)).willReturn(Optional.empty())

        client.perform(MockMvcRequestBuilders.get("/comments/99"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `creates a comment for location`() {
        val location = Location(42, "Main", "Bankster", LocationType.BankAccount)
        val commentDto = CommentDto(null, "Something")
        BDDMockito.given(commentService.saveForLocation(42, commentDto))
            .willReturn(Comment(11, "Something", location))

        client.perform(
            MockMvcRequestBuilders.post("/locations/42/comments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"text": "Something"}""")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/comments/11"))
    }

    @Test
    fun `creates a comment for transfer`() {
        val sourceLocation = Location(42, "Main", "Bankster", LocationType.BankAccount)
        val targetLocation = Location(21, "Secondary", "NuBank", LocationType.BankAccount)
        val transfer = Transfer(
            11,
            sourceLocation,
            targetLocation,
            Money(4, Currency.EUR),
            Money.zero(Currency.EUR),
            LocalDateTime.of(2021, 4, 5, 23, 5)
        )
        val commentDto = CommentDto(null, "Something")
        BDDMockito.given(commentService.saveForTransfer(11, commentDto))
            .willReturn(Comment(11, "Something", transfer))

        client.perform(
            MockMvcRequestBuilders.post("/transfers/11/comments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"text": "Something"}""")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/comments/11"))
    }

    @Test
    fun `creates a comment for currency trade`() {
        val location = Location(42, "Main", "Bankster", LocationType.BankAccount)
        val currencyTrade = CurrencyTrade(
            11,
            Currency.BTC,
            Currency.EUR,
            TradeType.Buy,
            Money(47000, Currency.EUR),
            0.0023,
            Money(1, Currency.EUR),
            LocalDateTime.of(2021, 4, 5, 23, 5),
            location
        )
        val commentDto = CommentDto(null, "Something")
        BDDMockito.given(commentService.saveForCurrencyTrade(11, commentDto))
            .willReturn(Comment(1, "Something", currencyTrade))

        client.perform(
            MockMvcRequestBuilders.post("/currency-trades/11/comments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"text": "Something"}""")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/comments/1"))
    }

    @Test
    fun `updates a comment`() {
        val location = Location(42, "Main", "Bankster", LocationType.BankAccount)
        val commentDto = CommentDto(14, "Something more")
        BDDMockito.given(commentService.save(commentDto))
            .willReturn(Comment(14, "Something more", location))

        client.perform(
            MockMvcRequestBuilders.put("/comments/14")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": 14, "text": "Something more"}""")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `does not update a comment when URL id does not match body id`() {
        client.perform(
            MockMvcRequestBuilders.put("/comments/14")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "144", "text": "Something more"}"""")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `deletes a comment`() {
        client.perform(MockMvcRequestBuilders.delete("/comments/1234"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}