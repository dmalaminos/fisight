package com.fisight.movement

import com.fisight.location.LocationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class MovementService(
    private val movementRepository: MovementRepository,
    private val locationRepository: LocationRepository,
    private val mapper: MovementMapper
) {
    fun findAllByLocation(locationId: Int): List<Movement> {
        return movementRepository.findAllByLocationId(locationId)
    }

    fun findById(id: Int): Optional<Movement> {
        return movementRepository.findById(id)
    }

    fun save(movementDto: MovementDto): Movement {
        val location = locationRepository.findById(movementDto.locationId)
        if (!location.isPresent) {
            throw IllegalArgumentException("Location was not found")
        }

        return movementRepository.save(mapper.toEntity(movementDto, location.get()))
    }

    fun deleteById(id: Int) {
        movementRepository.deleteById(id)
    }
}
