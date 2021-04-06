package com.fisight.currencyTrade

import com.fisight.location.Location
import org.springframework.stereotype.Component

@Component
class CurrencyTradeMapper {
    fun toEntity(currencyTradeDto: CurrencyTradeDto, location: Location): CurrencyTrade {
        return CurrencyTrade(
            currencyTradeDto.id ?: 0,
            currencyTradeDto.baseCurrency,
            currencyTradeDto.quoteCurrency,
            currencyTradeDto.tradeType,
            currencyTradeDto.pricePerBaseUnit,
            currencyTradeDto.quantity,
            currencyTradeDto.fee,
            currencyTradeDto.dateTraded,
            location
        )
    }

    fun toDto(currencyTrade: CurrencyTrade): CurrencyTradeDto {
        return CurrencyTradeDto(
            currencyTrade.id,
            currencyTrade.baseCurrency,
            currencyTrade.quoteCurrency,
            currencyTrade.tradeType,
            currencyTrade.pricePerBaseUnit,
            currencyTrade.quantity,
            currencyTrade.fee,
            currencyTrade.dateTraded,
            currencyTrade.location
        )
    }
}