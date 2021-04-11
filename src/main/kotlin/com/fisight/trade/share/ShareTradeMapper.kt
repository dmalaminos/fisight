package com.fisight.trade.share

import com.fisight.location.Location
import org.springframework.stereotype.Component

@Component
class ShareTradeMapper {
    fun toEntity(shareTradeDto: ShareTradeDto, location: Location): ShareTrade {
        return ShareTrade(
            shareTradeDto.id ?: 0,
            shareTradeDto.name,
            shareTradeDto.isinCode,
            shareTradeDto.exchange,
            shareTradeDto.ticker,
            shareTradeDto.tradeType,
            shareTradeDto.baseCurrency,
            shareTradeDto.quoteCurrency,
            shareTradeDto.pricePerBaseUnit,
            shareTradeDto.quantity,
            shareTradeDto.fee,
            shareTradeDto.dateTraded,
            location
        )
    }

    fun toDto(shareTrade: ShareTrade): ShareTradeDto {
        return ShareTradeDto(
            shareTrade.id,
            shareTrade.name,
            shareTrade.isinCode,
            shareTrade.exchange,
            shareTrade.ticker,
            shareTrade.tradeType,
            shareTrade.baseCurrency,
            shareTrade.quoteCurrency,
            shareTrade.pricePerBaseUnit,
            shareTrade.quantity,
            shareTrade.fee,
            shareTrade.dateTraded,
            shareTrade.location.id
        )
    }
}