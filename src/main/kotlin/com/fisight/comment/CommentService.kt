package com.fisight.comment

import com.fisight.location.Location
import com.fisight.location.LocationService
import com.fisight.trade.currency.CurrencyTradeService
import com.fisight.transfer.TransferService
import java.util.Optional
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val locationService: LocationService,
    private val transferService: TransferService,
    private val currencyTradeService: CurrencyTradeService
) {
    fun saveForLocation(locationId: Int, commentDto: CommentDto): Comment {
        return locationService.findById(locationId)
            .map { commentRepository.save(locationCommentDtoToEntity(commentDto, it)) }
            .orElseThrow { IllegalArgumentException("Location does not exist") }
    }

    fun saveForTransfer(transferId: Int, commentDto: CommentDto): Comment {
        return transferService.findById(transferId)
            .map { commentRepository.save(Comment(commentDto.id ?: 0, commentDto.text, it)) }
            .orElseThrow { IllegalArgumentException("Transfer does not exist") }
    }

    fun saveForCurrencyTrade(currencyTradeId: Int, commentDto: CommentDto): Comment {
        return currencyTradeService.findById(currencyTradeId)
            .map { commentRepository.save(Comment(commentDto.id ?: 0, commentDto.text, it)) }
            .orElseThrow { IllegalArgumentException("Currency trade does not exist") }
    }

    fun save(commentDto: CommentDto): Comment {
        if (commentDto.id == null) {
            throw IllegalArgumentException("Missing comment identifier")
        }

        return commentRepository.findById(commentDto.id).map {
            commentRepository.save(it.copy(text = commentDto.text))
        }.orElseThrow { IllegalArgumentException("Comment does not exist") }
    }

    fun deleteById(id: Int) {
        commentRepository.deleteById(id)
    }

    fun findById(id: Int): Optional<Comment> {
        return commentRepository.findById(id)
    }

    fun findAllForLocation(locationId: Int): List<Comment> {
        return commentRepository.findByCommentedEntityIdAndEntityType(locationId, "Location")
    }

    fun findAllForTransfer(transferId: Int): List<Comment> {
        return commentRepository.findByCommentedEntityIdAndEntityType(transferId, "Transfer")
    }

    fun findAllForCurrencyTrade(currencyTradeId: Int): List<Comment> {
        return commentRepository.findByCommentedEntityIdAndEntityType(currencyTradeId, "CurrencyTrade")
    }

    private fun locationCommentDtoToEntity(
        comment: CommentDto,
        location: Location
    ) = Comment(comment.id ?: 0, comment.text, location)
}
