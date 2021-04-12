package com.fisight.trade.share

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
class ShareTradeController(
    private val shareTradeService: ShareTradeService,
    private val mapper: ShareTradeMapper
) {
    @GetMapping("/trades/share/")
    fun getAll(): ResponseEntity<List<ShareTradeDto>> {
        return ResponseEntity.ok(shareTradeService.findAll().map { mapper.toDto(it) })
    }

    @GetMapping("/trades/share/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<ShareTradeDto> {
        return shareTradeService.findById(id)
            .map { ResponseEntity.ok(mapper.toDto(it)) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/trades/share/")
    fun save(
        @RequestBody shareTradeDto: ShareTradeDto
    ): ResponseEntity<Any> {
        val (shareTradeId) = shareTradeService.save(shareTradeDto)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/trades/share/{shareTradeId}").buildAndExpand(shareTradeId).toUri()
        ).build()
    }

    @PutMapping("/trades/share/{id}")
    fun update(
        @PathVariable("id") id: Int,
        @RequestBody shareTradeDto: ShareTradeDto
    ): ResponseEntity<Any> {
        if (shareTradeDto.id == id) {
            shareTradeService.save(shareTradeDto)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/trades/share/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        shareTradeService.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
