package com.fisight.account

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/financial-locations")
class FinancialLocationController(private val financialLocationRepository: FinancialLocationRepository) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<MutableIterable<FinancialLocation>> {
        return ResponseEntity.ok(financialLocationRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<FinancialLocation> {
        val account = financialLocationRepository.findById(id)
        return account.map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody financialLocation: FinancialLocation): ResponseEntity<Any> {
        //TODO: input validation
        val accountToSave = FinancialLocation(financialLocation.id, financialLocation.name, financialLocation.entityName)
        financialLocationRepository.save(accountToSave)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/financial-locations/{id}").buildAndExpand(accountToSave.id).toUri()
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
