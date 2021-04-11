package com.fisight.trade.currency

import java.time.LocalDateTime
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyTradeRepository : CrudRepository<CurrencyTrade, Int> {
    fun findByLocation_IdAndDateTradedBefore(
        locationId: Int,
        dateTraded: LocalDateTime = LocalDateTime.now()
    ): List<CurrencyTrade>
}