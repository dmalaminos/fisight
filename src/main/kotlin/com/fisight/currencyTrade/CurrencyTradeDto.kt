package com.fisight.currencyTrade

import com.fisight.financialLocation.FinancialLocation
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime

data class CurrencyTradeDto(
    val id: Int?,
    val baseCurrency: Currency,
    val quoteCurrency: Currency,
    val tradeType: CurrencyTradeType,
    val pricePerBaseUnit: Money,
    val quantity: Double,
    val fee: Money,
    val dateTraded: LocalDateTime,
    val location: FinancialLocation?
)