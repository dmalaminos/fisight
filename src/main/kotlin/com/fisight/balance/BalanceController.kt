package com.fisight.balance

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class BalanceController(private val balanceService: BalanceService) {
    @GetMapping("/balance/{locationId}")
    fun calculateForLocation(
        @PathVariable locationId: Int,
        @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) atDate: LocalDateTime
    ): ResponseEntity<Balance> {
        return ResponseEntity.ok(balanceService.calculateForLocation(locationId, atDate))
    }
}