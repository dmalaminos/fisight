package com.fisight.transfer

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
class TransferController(
    private val transferService: TransferService,
    private val mapper: TransferMapper
) {
    @GetMapping("/transfers/")
    fun getAll(): ResponseEntity<List<TransferDto>> {
        val list = transferService.findAll().map { mapper.toDto(it) }
        return ResponseEntity.ok(list)
    }

    @GetMapping("/locations/{locationId}/transfers/")
    fun getAllBySourceLocation(@PathVariable("locationId") locationId: Int): ResponseEntity<List<TransferDto>> {
        return ResponseEntity.ok(transferService.findAllBySourceLocation(locationId).map { mapper.toDto(it) })
    }

    @GetMapping("/transfers/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<TransferDto> {
        return transferService.findById(id)
            .map { ResponseEntity.ok(mapper.toDto(it)) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/transfers/")
    fun save(@RequestBody transfer: TransferDto): ResponseEntity<Any> {
        val (transferId) = transferService.save(transfer)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/transfers/{transferId}").buildAndExpand(transferId).toUri()
        ).build()
    }

    @PutMapping("/transfers/{id}")
    fun update(@PathVariable("id") id: Int, @RequestBody transfer: TransferDto): ResponseEntity<Any> {
        if (transfer.id == id) {
            transferService.save(transfer)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/transfers/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        transferService.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
