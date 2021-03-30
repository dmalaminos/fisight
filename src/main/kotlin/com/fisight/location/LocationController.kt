package com.fisight.location

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/locations")
class LocationController(private val locationRepository: LocationRepository) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<MutableIterable<Location>> {
        return ResponseEntity.ok(locationRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<Location> {
        val location = locationRepository.findById(id)
        return location.map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody location: Location): ResponseEntity<Any> {
        //TODO: input validation
        val locationToSave = Location(location.id, location.name, location.entityName)
        locationRepository.save(locationToSave)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/locations/{id}").buildAndExpand(locationToSave.id).toUri()
        ).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Int, @RequestBody location: Location): ResponseEntity<Any> {
        if (location.id == id) {
            locationRepository.save(location)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        locationRepository.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
