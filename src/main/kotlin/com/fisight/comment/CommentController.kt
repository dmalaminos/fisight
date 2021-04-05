package com.fisight.comment

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
class CommentController(private val commentService: CommentService) {
    @GetMapping("/locations/{locationId}/comments/")
    fun getAllForLocation(@PathVariable("locationId") locationId: Int): ResponseEntity<List<Comment>> {
        return ResponseEntity.ok(commentService.findAllForLocation(locationId))
    }

    @GetMapping("/transfers/{transferId}/comments/")
    fun getAllForTransfer(@PathVariable("transferId") transferId: Int): ResponseEntity<List<Comment>> {
        return ResponseEntity.ok(commentService.findAllForTransfer(transferId))
    }

    @GetMapping("/comments/{id}")
    fun getById(@PathVariable("id") id: Int): ResponseEntity<Comment> {
        val comment = commentService.findById(id)
        return comment.map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.badRequest().build())
    }

    @PostMapping("/locations/{locationId}/comments/")
    fun saveForLocation(
        @PathVariable("locationId") locationId: Int,
        @RequestBody comment: CommentDto
    ): ResponseEntity<Any> {
        val commentSaved = commentService.saveForLocation(locationId, comment)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/comments/{id}").buildAndExpand(commentSaved.id).toUri()
        ).build()
    }

    @PostMapping("/transfers/{transferId}/comments/")
    fun saveForTransfer(
        @PathVariable("transferId") transferId: Int,
        @RequestBody comment: CommentDto
    ): ResponseEntity<Any> {
        val commentSaved = commentService.saveForTransfer(transferId, comment)
        return ResponseEntity.created(
            UriComponentsBuilder.fromPath("/comments/{id}").buildAndExpand(commentSaved.id).toUri()
        ).build()
    }

    @PutMapping("/comments/{id}")
    fun update(@PathVariable("id") id: Int, @RequestBody comment: CommentDto): ResponseEntity<Any> {
        if (comment.id == id) {
            commentService.save(comment)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/comments/{id}")
    fun delete(@PathVariable("id") id: Int): ResponseEntity<Any> {
        commentService.deleteById(id)
        return ResponseEntity.ok().build()
    }
}
