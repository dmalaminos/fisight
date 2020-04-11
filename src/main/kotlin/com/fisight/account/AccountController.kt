package com.fisight.account

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/accounts")
class AccountController(private val accountRepository: AccountRepository) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<MutableIterable<Account>> {
        return ResponseEntity.ok(accountRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<Account> {
        val account = accountRepository.findById(AccountId(id))
        return account.map { ResponseEntity.ok(it) }
                .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody account: Account): ResponseEntity<Any> {
        //TODO: input validation
        val accountToSave = Account(AccountId(), account.name, account.bankName, account.capital)
        accountRepository.save(accountToSave)
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/accounts/{id}").buildAndExpand(accountToSave.id).toUri()).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: String, @RequestBody account: Account): ResponseEntity<Any> {
        if (account.id == AccountId(id)) {
            accountRepository.save(account)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Any> {
        accountRepository.deleteById(AccountId(id))
        return ResponseEntity.ok().build()
    }
}
