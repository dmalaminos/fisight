package com.fisight.trade.currency

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
class CurrencyTradeController(
    private val currencyTradeService: CurrencyTradeService,
    private val mapper: CurrencyTradeMapper
) {
    @GetMapping("/trades/currency/")
    fun getAll(): ResponseEntity<List<CurrencyTradeDto>> {
        return ResponseEntity.ok(currencyTradeService.findAll().map { mapper.toDto(it) })
    }

    @GetMapping("/trades/currency/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<CurrencyTradeDto> {
        return currencyTradeService.findById(id)
            .map { ResponseEntity.ok(mapper.toDto(it)) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/trades/currency/")
    fun save(@RequestBody currencyTradeDto: CurrencyTradeDto): ResponseEntity<Any> {
        val (currencyTradeId) = currencyTradeService.save(currencyTradeDto)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/trades/currency/{currencyTradeId}").buildAndExpand(currencyTradeId).toUri()
        ).build()
    }

    @PutMapping("/trades/currency/{id}")
    fun update(
        @PathVariable("id") id: Int,
        @RequestBody currencyTradeDto: CurrencyTradeDto
    ): ResponseEntity<Any> {
        if (currencyTradeDto.id == id) {
            currencyTradeService.save(currencyTradeDto)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/trades/currency/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        currencyTradeService.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
