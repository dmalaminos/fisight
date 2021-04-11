package com.fisight.comment

import com.fisight.location.Location
import com.fisight.location.LocationService
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import com.fisight.trade.currency.CurrencyTradeService
import com.fisight.transfer.Transfer
import com.fisight.transfer.TransferService
import java.time.LocalDateTime
import java.util.Optional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever


class CommentServiceTest {
    private lateinit var commentService: CommentService

    @Mock
    private val commentRepository: CommentRepository = mock()

    @Mock
    private val locationService: LocationService = mock()

    @Mock
    private val transferService: TransferService = mock()

    @Mock
    private val currencyTradeService: CurrencyTradeService = mock()

    @BeforeEach
    fun setUp() {
        commentService = CommentService(commentRepository, locationService, transferService, currencyTradeService)
    }

    @Test
    fun `gets all comments by location`() {
        commentService.findAllForLocation(12)

        verify(commentRepository).findByCommentedEntityIdAndEntityType(12, "Location")
        verifyNoMoreInteractions(commentRepository)
        verifyZeroInteractions(locationService)
    }

    @Test
    fun `gets all comments by transfer`() {
        commentService.findAllForTransfer(11)

        verify(commentRepository).findByCommentedEntityIdAndEntityType(11, "Transfer")
        verifyNoMoreInteractions(commentRepository)
        verifyZeroInteractions(transferService)
    }

    @Test
    fun `gets a single comment by id`() {
        commentService.findById(2)

        verify(commentRepository).findById(2)
        verifyNoMoreInteractions(commentRepository)
        verifyZeroInteractions(locationService)
    }

    @Test
    fun `saves a new comment for location`() {
        val location = Location(42, "name", "entityName", LocationType.BankAccount)
        val comment = Comment(0, "Something", location)
        val commentDto = CommentDto(null, "Something")
        whenever(locationService.findById(42)).thenReturn(Optional.of(location))
        whenever(commentRepository.save(comment)).thenReturn(comment.copy(id = 1))

        commentService.saveForLocation(42, commentDto)

        verify(locationService).findById(42)
        verify(commentRepository).save(comment)
        verifyNoMoreInteractions(locationService)
        verifyNoMoreInteractions(commentRepository)
    }

    @Test
    fun `saves a new comment for transfer`() {
        val sourceLocation = Location(42, "location", "entity", LocationType.BankAccount)
        val targetLocation = Location(21, "anotherLocation", "anotherEntity", LocationType.BankAccount)
        val transfer = Transfer(
            11,
            sourceLocation,
            targetLocation,
            Money(4, Currency.EUR),
            Money.zero(Currency.EUR),
            LocalDateTime.of(2021, 4, 5, 23, 0)
        )
        val comment = Comment(0, "Something", transfer)
        val commentDto = CommentDto(null, "Something")
        whenever(transferService.findById(11)).thenReturn(Optional.of(transfer))
        whenever(commentRepository.save(comment)).thenReturn(comment.copy(id = 1))

        commentService.saveForTransfer(11, commentDto)

        verify(transferService).findById(11)
        verify(commentRepository).save(comment)
        verifyNoMoreInteractions(transferService)
        verifyNoMoreInteractions(commentRepository)
        verifyZeroInteractions(locationService)
    }

    @Test
    fun `updates existing comment`() {
        val location = Location(42, "name", "entityName", LocationType.BankAccount)
        val comment = Comment(12, "Something", location)
        val newComment = Comment(12, "Something more", location)
        val commentDto = CommentDto(12, "Something more")
        whenever(commentRepository.findById(12)).thenReturn(Optional.of(comment))
        whenever(commentRepository.save(newComment)).thenReturn(newComment)

        commentService.save(commentDto)

        verify(commentRepository).findById(12)
        verify(commentRepository).save(newComment)
        verifyNoMoreInteractions(locationService)
        verifyNoMoreInteractions(commentRepository)
    }

    @Test
    fun `deletes a comment by id`() {
        commentService.deleteById(1)

        verify(commentRepository).deleteById(1)
        verifyNoMoreInteractions(commentRepository)
        verifyZeroInteractions(locationService)
    }
}