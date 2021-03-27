package com.fisight.currencyTrade

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
@RequestMapping("/currency-trades")
class CurrencyTradeController(private val currencyTradeRepository: CurrencyTradeRepository) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<MutableIterable<CurrencyTrade>> {
        return ResponseEntity.ok(currencyTradeRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<CurrencyTrade> {
        val currencyTrade = currencyTradeRepository.findById(id)
        return currencyTrade.map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody currencyTrade: CurrencyTrade): ResponseEntity<Any> {
        val currencyTradeToSave = CurrencyTrade(
            currencyTrade.id,
            currencyTrade.baseCurrency,
            currencyTrade.quoteCurrency,
            currencyTrade.tradeType,
            currencyTrade.pricePerBaseUnit,
            currencyTrade.quantity,
            currencyTrade.fee,
            currencyTrade.dateTraded,
            currencyTrade.location
        )
        currencyTradeRepository.save(currencyTradeToSave)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/currency-trades/{id}").buildAndExpand(currencyTradeToSave.id).toUri()
        ).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Int, @RequestBody currencyTrade: CurrencyTrade): ResponseEntity<Any> {
        if (currencyTrade.id == id) {
            currencyTradeRepository.save(currencyTrade)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        currencyTradeRepository.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
