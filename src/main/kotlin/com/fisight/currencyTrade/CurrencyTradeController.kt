package com.fisight.currencyTrade

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
class CurrencyTradeController(private val currencyTradeService: CurrencyTradeService) {
    @GetMapping("/currency-trades/")
    fun getAll(): ResponseEntity<List<CurrencyTradeDto>> {
        return ResponseEntity.ok(currencyTradeService.findAll().toList())
    }

    @GetMapping("/currency-trades/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<CurrencyTradeDto> {
        val currencyTrade = currencyTradeService.findById(id)
        return currencyTrade.map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/locations/{locationId}/currency-trades/")
    fun save(
        @PathVariable("locationId") locationId: Int,
        @RequestBody currencyTrade: CurrencyTradeDto
    ): ResponseEntity<Any> {
        val (currencyTradeId) = currencyTradeService.save(currencyTrade, locationId)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/currency-trades/{currencyTradeId}").buildAndExpand(currencyTradeId).toUri()
        ).build()
    }

    @PutMapping("/locations/{locationId}/currency-trades/{id}")
    fun update(
        @PathVariable("id") id: Int,
        @PathVariable("locationId") locationId: Int,
        @RequestBody currencyTradeDto: CurrencyTradeDto
    ): ResponseEntity<Any> {
        if (currencyTradeDto.id == id) {
            currencyTradeService.save(currencyTradeDto, locationId)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/currency-trades/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        currencyTradeService.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
