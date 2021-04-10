package com.fisight.movement

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MovementRepository : CrudRepository<Movement, Int> {
    fun findAllByLocationId(locationId: Int): List<Movement>
}