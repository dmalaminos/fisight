package com.fisight.trade.currency

import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime

data class CurrencyTradeDto(
    val id: Int?,
    val baseCurrency: Currency,
    val quoteCurrency: Currency,
    val tradeType: TradeType,
    val pricePerBaseUnit: Money,
    val quantity: Double,
    val fee: Money,
    val dateTraded: LocalDateTime,
    val locationId: Int
)