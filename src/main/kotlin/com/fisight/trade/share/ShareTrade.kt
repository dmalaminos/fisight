package com.fisight.trade.share

import com.fisight.comment.Commentable
import com.fisight.location.Location
import com.fisight.money.Currency
import com.fisight.money.Money
import com.fisight.trade.currency.TradeType
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class ShareTrade(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Int,
    val name: String,
    val isinCode: String,
    val exchange: String,
    val ticker: String,
    val tradeType: TradeType,
    @Enumerated(EnumType.STRING)
    val baseCurrency: Currency,
    @Enumerated(EnumType.STRING)
    val quoteCurrency: Currency,
    val pricePerBaseUnit: Money,
    val quantity: Double,
    val fee: Money,
    val dateTraded: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY)
    val location: Location
) : Commentable