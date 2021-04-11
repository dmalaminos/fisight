package com.fisight.trade.currency

import com.fisight.comment.Commentable
import com.fisight.location.Location
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

enum class TradeType {
    Buy,
    Sell
}

@Entity
data class CurrencyTrade(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Int,
    @Enumerated(EnumType.STRING)
    val baseCurrency: Currency,
    @Enumerated(EnumType.STRING)
    val quoteCurrency: Currency,
    @Enumerated(EnumType.STRING)
    val tradeType: TradeType,
    val pricePerBaseUnit: Money,
    val quantity: Double,
    val fee: Money,
    val dateTraded: LocalDateTime,
    @ManyToOne(fetch = FetchType.EAGER)
    val location: Location,
) : Commentable