package com.fisight.fisight.financialasset

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
@RequestMapping("/assets")
class FinancialAssetController(private val financialAssetRepository: FinancialAssetRepository) {
    @GetMapping("/")
    fun getAll(): ResponseEntity<MutableList<FinancialAsset>> {
        return ResponseEntity.ok(financialAssetRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<FinancialAsset> {
        val asset = financialAssetRepository.findById(id)
        return asset.map { ResponseEntity.ok(it) }
                .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/")
    fun save(@RequestBody asset: FinancialAsset): ResponseEntity<Any> {
        //TODO: asset creation command
        // private val commandGateway: CommandGateway
        // val newFinancialAssetId = commandGateway.sendAndWait<String>("123", asset.name)
        financialAssetRepository.insert(asset)
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/assets/{id}").buildAndExpand("123").toUri()).build()
//        return ResponseEntity.status(HttpStatus.CONFLICT).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: String, @RequestBody asset: FinancialAsset): ResponseEntity<Any> {
        if (asset.id == id) {
            financialAssetRepository.save(asset)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Any> {
        financialAssetRepository.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
