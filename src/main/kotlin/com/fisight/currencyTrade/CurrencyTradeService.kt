package com.fisight.currencyTrade

import com.fisight.location.Location
import com.fisight.location.LocationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CurrencyTradeService(
    private val currencyTradeRepository: CurrencyTradeRepository,
    private val locationRepository: LocationRepository
) {
    fun findAll(): List<CurrencyTradeDto> {
        return currencyTradeRepository.findAll()
            .map { currencyTradeEntityToDto(it) }
            .toList()
    }

    fun findById(id: Int): Optional<CurrencyTradeDto> {
        return currencyTradeRepository.findById(id).map { currencyTradeEntityToDto(it) }
    }

    fun save(currencyTradeDto: CurrencyTradeDto, locationId: Int): CurrencyTradeDto {
        val location = locationRepository.findById(locationId)
        if (location.isPresent) {
            val currencyTrade = currencyTradeDtoToEntity(currencyTradeDto, location.get())
            return currencyTradeEntityToDto(currencyTradeRepository.save(currencyTrade))
        }
        throw IllegalArgumentException("Location does not exist")
    }

    fun deleteById(id: Int) {
        currencyTradeRepository.deleteById(id)
    }

    fun findAllForLocationBeforeDate(locationId: Int, atDate: LocalDateTime): List<CurrencyTrade> {
        return currencyTradeRepository.findByLocation_IdAndDateTradedBefore(locationId, atDate)
    }

    private fun currencyTradeDtoToEntity(currencyTradeDto: CurrencyTradeDto, location: Location): CurrencyTrade {
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