package com.fisight.trade.share

import com.fisight.location.LocationRepository
import java.util.Optional
import org.springframework.stereotype.Service

@Service
class ShareTradeService(
    private val shareTradeRepository: ShareTradeRepository,
    private val locationRepository: LocationRepository,
    private val mapper: ShareTradeMapper
) {
    fun findById(id: Int): Optional<ShareTrade> {
        return shareTradeRepository.findById(id)
    }

    fun findAll(): List<ShareTrade> {
        return shareTradeRepository.findAll().toList()
    }

    fun findAllForLocation(locationId: Int): List<ShareTrade> {
        return shareTradeRepository.findByLocationId(locationId)
    }

    fun save(currencyTradeDto: ShareTradeDto, locationId: Int): ShareTrade {
        val location = locationRepository.findById(locationId)
        if (location.isPresent) {
            val currencyTrade = mapper.toEntity(currencyTradeDto, location.get())
            return shareTradeRepository.save(currencyTrade)
        }
        throw IllegalArgumentException("Location does not exist")
    }

    fun deleteById(id: Int) {
        shareTradeRepository.deleteById(id)
    }
}