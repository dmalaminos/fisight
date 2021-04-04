package com.fisight.currencyTrade

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CurrencyTradeRepository : CrudRepository<CurrencyTrade, Int> {
    fun findByLocation_IdAndDateTradedBefore(
        locationId: Int,
        dateTraded: LocalDateTime = LocalDateTime.now()
    ): List<CurrencyTrade>
}