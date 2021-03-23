package com.fisight.account

import com.fisight.capital.Capital
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

@WebMvcTest(AccountController::class)
class AccountTests {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var accountRepository: AccountRepository

    @Test
    fun canGetAccounts() {
        val accounts = arrayOf(
            Account(1, "Main", "Bankster", Capital(3000.0)),
            Account(2, "Savings", "Altbank", Capital(7000.0))
        )
        given(accountRepository.findAll()).willReturn(accounts.toList())

        client.perform(get("/accounts/"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("Main"))
            .andExpect(jsonPath("$[0].bankName").value("Bankster"))
            .andExpect(jsonPath("$[0].capital.amount").value("3000.0"))
            .andExpect(jsonPath("$[1].name").value("Savings"))
            .andExpect(jsonPath("$[1].bankName").value("Altbank"))
            .andExpect(jsonPath("$[1].capital.amount").value("7000.0"))
    }

    @Test
    fun canGetAccountById() {
        val account = Account(123, "Main", "Bankster", Capital(3000.0))
        given(accountRepository.findById(123)).willReturn(Optional.of(account))

        client.perform(get("/accounts/123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Main"))
            .andExpect(jsonPath("$.bankName").value("Bankster"))
            .andExpect(jsonPath("$.capital.amount").value("3000.0"))
    }

    @Test
    fun cannotGetAccountById_whenIdDoesNotExist() {
        given(accountRepository.findById(123)).willReturn(Optional.empty())

        client.perform(get("/accounts/123"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun canCreateAccount() {
        client.perform(
            post("/accounts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "Main", "bankName":"Bankster", "capital": {"amount": 3000.0}}""")
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/accounts/0"))
    }

    @Test
    fun canUpdateAccount() {
        val account = Account(123, "Main", "Bankster", Capital(3000.0))
        given(accountRepository.findById(123)).willReturn(Optional.of(account))
        given(accountRepository.save(account)).willReturn(account)

        client.perform(
            put("/accounts/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "123", "name": "Secondary", "bankName":"Bankster", "capital": {"amount": 3000.0}}""")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun cannotUpdateAccount_whenUrlIdDoesNotMatchBodyId() {
        val account = Account(123, "Main", "Bankster", Capital(3000.0))
        given(accountRepository.findById(123)).willReturn(Optional.of(account))

        client.perform(
            put("/accounts/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id": "234", "name": "Secondary", "bankName":"Bankster", "capital": {"amount": 3000.0}}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun canDeleteAccount() {
        client.perform(delete("/accounts/1234"))
            .andExpect(status().isOk)
    }
}
