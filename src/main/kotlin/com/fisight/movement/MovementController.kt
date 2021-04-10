package com.fisight.movement

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
class MovementController(
    private val movementService: MovementService,
    private val mapper: MovementMapper
) {
    @GetMapping("/locations/{locationId}/movements/")
    fun getAllByLocation(@PathVariable("locationId") locationId: Int): ResponseEntity<List<MovementDto>> {
        return ResponseEntity.ok(movementService.findAllByLocation(locationId).map { mapper.toDto(it) })
    }

    @GetMapping("/movements/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<MovementDto> {
        return movementService.findById(id)
            .map { ResponseEntity.ok(mapper.toDto(it)) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/movements/")
    fun save(@RequestBody movementDto: MovementDto): ResponseEntity<Any> {
        val (movementId) = movementService.save(movementDto)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/movements/{movementId}").buildAndExpand(movementId).toUri()
        ).build()
    }

    @PutMapping("/movements/{id}")
    fun update(@PathVariable("id") id: Int, @RequestBody movementDto: MovementDto): ResponseEntity<Any> {
        if (movementDto.id == id) {
            movementService.save(movementDto)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/movements/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        movementService.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
