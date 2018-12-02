package com.fisight.fisight.account

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/accounts")
class AccountController(private val accountRepository: AccountRepository, private val commandGateway: CommandGateway, private val queryGateway: QueryGateway) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<MutableList<Account>> {
        return ResponseEntity.ok(accountRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<Account> {
        val account = accountRepository.findById(id)
        return account.map { ResponseEntity.ok(it) }
                .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody account: Account): ResponseEntity<Any> {
        //TODO: input validation
        //TODO: add host to created location builder
        val newAccountId = AccountId() //commandGateway.sendAndWait<AccountId>(CreateAccountCommand(AccountId(), account.name, account.bankName))
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/accounts/{id}").buildAndExpand(newAccountId.identifier).toUri()).build()
//        return ResponseEntity.status(HttpStatus.CONFLICT).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: String, @RequestBody account: Account): ResponseEntity<Any> {
        if (account.id.identifier == id) {
            accountRepository.save(account)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Any> {
        accountRepository.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
