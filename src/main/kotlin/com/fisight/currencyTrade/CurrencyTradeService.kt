package com.fisight.currencyTrade

import com.fisight.location.LocationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CurrencyTradeService(
    private val currencyTradeRepository: CurrencyTradeRepository,
    private val locationRepository: LocationRepository,
    private val mapper: CurrencyTradeMapper
) {
    fun findById(id: Int): Optional<CurrencyTrade> {
        return currencyTradeRepository.findById(id)
    }

    fun findAll(): List<CurrencyTrade> {
        return currencyTradeRepository.findAll().toList()
    }

    fun findAllForLocationBeforeDate(locationId: Int, atDate: LocalDateTime): List<CurrencyTrade> {
        return currencyTradeRepository.findByLocation_IdAndDateTradedBefore(locationId, atDate)
    }

    fun save(currencyTradeDto: CurrencyTradeDto, locationId: Int): CurrencyTrade {
        val location = locationRepository.findById(locationId)
        if (location.isPresent) {
            val currencyTrade = mapper.toEntity(currencyTradeDto, location.get())
            return currencyTradeRepository.save(currencyTrade)
        }
        throw IllegalArgumentException("Location does not exist")
    }

    fun deleteById(id: Int) {
        currencyTradeRepository.deleteById(id)
    }
}