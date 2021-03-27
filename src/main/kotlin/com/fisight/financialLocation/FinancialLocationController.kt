package com.fisight.financialLocation

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
class FinancialLocationController(private val financialLocationRepository: FinancialLocationRepository) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<MutableIterable<FinancialLocation>> {
        return ResponseEntity.ok(financialLocationRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<FinancialLocation> {
        val financialLocation = financialLocationRepository.findById(id)
        return financialLocation.map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody financialLocation: FinancialLocation): ResponseEntity<Any> {
        //TODO: input validation
        val financialLocationToSave = FinancialLocation(financialLocation.id, financialLocation.name, financialLocation.entityName)
        financialLocationRepository.save(financialLocationToSave)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/locations/{id}").buildAndExpand(financialLocationToSave.id).toUri()
        ).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Int, @RequestBody financialLocation: FinancialLocation): ResponseEntity<Any> {
        if (financialLocation.id == id) {
            financialLocationRepository.save(financialLocation)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        financialLocationRepository.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
