package com.fisight.location

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever


class LocationServiceTest {
    private lateinit var locationService: LocationService

    @Mock
    private val locationRepository: LocationRepository = mock()

    @BeforeEach
    fun setUp() {
        locationService = LocationService(locationRepository)
    }

    @Test
    fun `gets all locations`() {
        locationService.findAll()

        verify(locationRepository).findAll()
        verifyNoMoreInteractions(locationRepository)
    }

    @Test
    fun `gets a single location by id`() {
        locationService.findById(2)

        verify(locationRepository).findById(2)
        verifyNoMoreInteractions(locationRepository)
    }

    @Test
    fun `saves a new location`() {
        val location = Location(0, "name", "entityName")
        whenever(locationRepository.save(location)).thenReturn(location.copy(id = 1))

        locationService.save(location)

        verify(locationRepository).save(location)
        verifyNoMoreInteractions(locationRepository)
    }

    @Test
    fun `deletes a location by id`() {
        locationService.deleteById(1)

        verify(locationRepository).deleteById(1)
        verifyNoMoreInteractions(locationRepository)
    }
}