package com.fisight.currencyTrade

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
class CurrencyTradeController(
    private val currencyTradeService: CurrencyTradeService,
    private val mapper: CurrencyTradeMapper
) {
    @GetMapping("/currency-trades/")
    fun getAll(): ResponseEntity<List<CurrencyTradeDto>> {
        return ResponseEntity.ok(currencyTradeService.findAll().map { mapper.toDto(it) })
    }

    @GetMapping("/currency-trades/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<CurrencyTradeDto> {
        return currencyTradeService.findById(id)
            .map { ResponseEntity.ok(mapper.toDto(it)) }
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
