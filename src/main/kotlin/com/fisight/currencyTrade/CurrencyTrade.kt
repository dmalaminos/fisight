package com.fisight.currencyTrade

import com.fisight.financialLocation.FinancialLocation
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

enum class CurrencyTradeType {
    Buy,
    Sell
}

@Entity
data class CurrencyTrade(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Enumerated(EnumType.STRING)
    val baseCurrency: Currency,
    @Enumerated(EnumType.STRING)
    val quoteCurrency: Currency,
    @Enumerated(EnumType.STRING)
    val tradeType: CurrencyTradeType,
    val pricePerBaseUnit: Money,
    val quantity: Double,
    val fee: Money,
    val dateTraded: LocalDateTime,
    @ManyToOne(fetch = FetchType.EAGER)
    val location: FinancialLocation,
)