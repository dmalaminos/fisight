package com.fisight.fisight.account

import com.fisight.fisight.capital.Capital
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.common.IdentifierFactory
import org.axonframework.queryhandling.QueryGateway
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@RunWith(SpringRunner::class)
@WebMvcTest(AccountController::class)
class AccountTests {
    @Autowired
    private lateinit var client: MockMvc

    @MockBean
    private lateinit var accountRepository: AccountRepository

    @MockBean
    private lateinit var queryGateway: QueryGateway

    @MockBean
    private lateinit var commandGateway: CommandGateway

    @MockBean
    private lateinit var identifierFactory: IdentifierFactory

    @Test
    fun canGetAccounts() {
        val accounts = arrayOf(
                Account(AccountId("1"), "Main", "Bankster", Capital(3000.0)),
                Account(AccountId("2"), "Savings", "Altbank", Capital(7000.0)))
        given(accountRepository.findAll()).willReturn(accounts.toList())

        client.perform(get("/accounts/"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].name").value("Main"))
                .andExpect(jsonPath("$[0].bankName").value("Bankster"))
                .andExpect(jsonPath("$[0].capital.amount").value("3000.0"))
                .andExpect(jsonPath("$[1].name").value("Savings"))
                .andExpect(jsonPath("$[1].bankName").value("Altbank"))
                .andExpect(jsonPath("$[1].capital.amount").value("7000.0"))
    }

    @Test
    fun canGetAccountById() {
        val account = Account(AccountId("123"), "Main", "Bankster", Capital(3000.0))
        given(accountRepository.findById("123")).willReturn(Optional.of(account))

        client.perform(get("/accounts/123"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.name").value("Main"))
                .andExpect(jsonPath("$.bankName").value("Bankster"))
                .andExpect(jsonPath("$.capital.amount").value("3000.0"))
    }

    @Test
    fun cannotGetAccountById_whenIdDoesNotExist() {
        given(accountRepository.findById("123")).willReturn(Optional.empty())

        client.perform(get("/accounts/123"))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun canCreateAccount() {
        given(commandGateway.sendAndWait<AccountId>(ArgumentMatchers.any(CreateAccountCommand::class.java))).willReturn(AccountId("123"))
        client.perform(post("/accounts/")
                .contentType(APPLICATION_JSON_UTF8)
                .content("""{"name": "Main", "bankName":"Bankster", "capital": {"amount": 3000.0}}"""))
                .andExpect(status().isCreated)
                .andExpect(header().string("Location", "/accounts/123"))
//        TODO: workaround AccountId mocking
//        Mockito.verify(commandGateway, times(1))
//                .sendAndWait<AccountId>(CreateAccountCommand(AccountId(), "Main", "Bankster", Capital(3000.0)))
    }

    @Test
    fun canUpdateAccount() {
        val account = Account(AccountId("123"), "Main", "Bankster", Capital(3000.0))
        given(accountRepository.findById("123")).willReturn(Optional.of(account))
        given(accountRepository.save(account)).willReturn(account)

        client.perform(put("/accounts/123")
                .contentType(APPLICATION_JSON_UTF8)
                .content("""{"id":{"identifier": "123"}, "name": "Secondary", "bankName":"Bankster", "capital": {"amount": 3000.0}}"""))
                .andExpect(status().isOk)
    }

    @Test
    fun cannotUpdateAccount_whenUrlIdDoesNotMatchBodyId() {
        val account = Account(AccountId("123"), "Main", "Bankster", Capital(3000.0))
        given(accountRepository.findById("123")).willReturn(Optional.of(account))

        client.perform(put("/accounts/123")
                .contentType(APPLICATION_JSON_UTF8)
                .content("""{"id":{"identifier": "234"}, "name": "Secondary", "bankName":"Bankster", "capital": {"amount": 3000.0}}"""))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun canDeleteAccount() {
        client.perform(delete("/accounts/1234"))
                .andExpect(status().isOk)
    }
}
