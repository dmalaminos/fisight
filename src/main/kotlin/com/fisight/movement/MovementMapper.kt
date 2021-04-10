package com.fisight.movement

import com.fisight.location.Location
import org.springframework.stereotype.Component

@Component
class MovementMapper {
    fun toEntity(movementDto: MovementDto, location: Location): Movement {
        return Movement(
            movementDto.id ?: 0,
            movementDto.amount,
            movementDto.direction,
            location,
            movementDto.type,
            movementDto.dateMoved
        )
    }

    fun toDto(movement: Movement): MovementDto {
        return MovementDto(
            movement.id,
            movement.amount,
            movement.direction,
            movement.location.id,
            movement.type,
            movement.dateMoved
        )
    }
}
