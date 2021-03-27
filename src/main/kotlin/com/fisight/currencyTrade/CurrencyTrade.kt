package com.fisight.currencyTrade

import com.fisight.financialLocation.FinancialLocation
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne

enum class CurrencyTradeType {
    Buy,
    Sell
}

@Entity
data class CurrencyTrade(
    @Id
    val id: Int,
    val baseCurrency: Currency,
    val quoteCurrency: Currency,
    val tradeType: CurrencyTradeType,
    val pricePerBaseUnit: Money,
    val quantity: Double,
    val fee: Money,
    val dateTraded: LocalDateTime,
    @ManyToOne(fetch = FetchType.EAGER)
    val location: FinancialLocation,
)
