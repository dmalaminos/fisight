package com.fisight.location

import org.springframework.stereotype.Service
import java.util.*

@Service
class LocationService(private val locationRepository: LocationRepository) {
    fun findAll(): List<Location> = locationRepository.findAll().toList()

    fun findById(id: Int): Optional<Location> = locationRepository.findById(id)

    fun save(location: Location): Location {
        return locationRepository.save(location)
    }

    fun deleteById(id: Int) = locationRepository.deleteById(id)
}