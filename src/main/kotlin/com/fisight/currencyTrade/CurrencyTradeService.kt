package com.fisight.currencyTrade

import com.fisight.financialLocation.FinancialLocation
import com.fisight.financialLocation.FinancialLocationRepository
import java.util.*
import org.springframework.stereotype.Service

@Service
class CurrencyTradeService(
    private val currencyTradeRepository: CurrencyTradeRepository,
    private val locationRepository: FinancialLocationRepository
) {
    fun getAll(): List<CurrencyTradeDto> {
        return currencyTradeRepository.findAll()
            .map { currencyTradeEntityToDto(it) }
            .toList()
    }

    fun getById(id: Int): Optional<CurrencyTradeDto> {
        return currencyTradeRepository.findById(id).map { currencyTradeEntityToDto(it) }
    }

    fun save(currencyTradeDto: CurrencyTradeDto, locationId: Int): CurrencyTradeDto {
        val financialLocation = locationRepository.findById(locationId)
        if (financialLocation.isPresent) {
            val currencyTrade = currencyTradeDtoToEntity(currencyTradeDto, financialLocation.get())
            return currencyTradeEntityToDto(currencyTradeRepository.save(currencyTrade))
        }
        throw IllegalArgumentException("Location does not exist")
    }

    fun deleteById(id: Int) {
        currencyTradeRepository.deleteById(id)
    }

    private fun currencyTradeDtoToEntity(currencyTradeDto: CurrencyTradeDto, financialLocation: FinancialLocation): CurrencyTrade {
        return CurrencyTrade(
            currencyTradeDto.id ?: 0,
            currencyTradeDto.baseCurrency,
            currencyTradeDto.quoteCurrency,
            currencyTradeDto.tradeType,
            currencyTradeDto.pricePerBaseUnit,
            currencyTradeDto.quantity,
            currencyTradeDto.fee,
            currencyTradeDto.dateTraded,
            financialLocation
            )
    }

    private fun currencyTradeEntityToDto(currencyTrade: CurrencyTrade): CurrencyTradeDto {
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