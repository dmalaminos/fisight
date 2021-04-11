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
class LocationController(private val locationService: LocationService) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<List<Location>> {
        return ResponseEntity.ok(locationService.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<Location> {
        val location = locationService.findById(id)
        return location.map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody location: Location): ResponseEntity<Any> {
        val locationToSave = Location(0, location.name, location.entityName, LocationType.BankAccount)
        val locationSaved = locationService.save(locationToSave)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/locations/{id}").buildAndExpand(locationSaved.id).toUri()
        ).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Int, @RequestBody location: Location): ResponseEntity<Any> {
        if (location.id == id) {
            locationService.save(location)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        locationService.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
