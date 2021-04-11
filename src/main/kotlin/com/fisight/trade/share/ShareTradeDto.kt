package com.fisight.trade.share

import com.fisight.money.Currency
import com.fisight.money.Money
import com.fisight.trade.currency.TradeType
import java.time.LocalDateTime

data class ShareTradeDto(
    val id: Int?,
    val name: String,
    val isinCode: String,
    val exchange: String,
    val ticker: String,
    val tradeType: TradeType,
    val baseCurrency: Currency,
    val quoteCurrency: Currency,
    val pricePerBaseUnit: Money,
    val quantity: Double,
    val fee: Money,
    val dateTraded: LocalDateTime,
    val locationId: Int
)
