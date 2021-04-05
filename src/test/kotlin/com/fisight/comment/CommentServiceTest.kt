package com.fisight.comment

import com.fisight.location.Location
import com.fisight.location.LocationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.*
import java.util.*


class CommentServiceTest {
    private lateinit var commentService: CommentService

    @Mock
    private val commentRepository: CommentRepository = mock()

    @Mock
    private val locationService: LocationService = mock()

    @BeforeEach
    fun setUp() {
        commentService = CommentService(commentRepository, locationService)
    }

    @Test
    fun `gets all comments by location`() {
        commentService.findAllForLocation(12)

        verify(commentRepository).findByCommentedEntityIdAndEntityType(12, "Location")
        verifyNoMoreInteractions(commentRepository)
        verifyZeroInteractions(locationService)
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
        val location = Location(42, "name", "entityName")
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
    fun `updates existing comment`() {
        val location = Location(42, "name", "entityName")
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