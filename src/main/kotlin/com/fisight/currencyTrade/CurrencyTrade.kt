package com.fisight.currencyTrade

import com.fisight.comment.Commentable
import com.fisight.location.Location
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import javax.persistence.*

enum class CurrencyTradeType {
    Buy,
    Sell
}

@Entity
data class CurrencyTrade(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Int = 0,
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
    val location: Location,
) : Commentable