package com.fisight.movement

import com.fisight.location.Location
import com.fisight.location.LocationRepository
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import java.util.Optional
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever


class MovementServiceTest {
    private lateinit var movementService: MovementService

    @Mock
    private val movementRepository: MovementRepository = mock()

    @Mock
    private val locationRepository: LocationRepository = mock()

    @Mock
    private val mapper: MovementMapper = mock()

    @BeforeEach
    fun setUp() {
        movementService = MovementService(movementRepository, locationRepository, mapper)
    }

    @Test
    fun `gets all movements by location`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movements = listOf(
            Movement(
                1,
                Money(300, Currency.EUR),
                MovementDirection.Inbound,
                location,
                MovementType.Wage,
                LocalDateTime.of(2021, 4, 8, 23, 56)
            ),
            Movement(
                2,
                Money(5, Currency.EUR),
                MovementDirection.Outbound,
                location,
                MovementType.Expense,
                LocalDateTime.of(2021, 4, 8, 22, 23)
            )
        )

        whenever(movementRepository.findAllByLocationId(5)).thenReturn(movements)

        val actual = movementService.findAllByLocation(5)

        verify(movementRepository).findAllByLocationId(5)
        Assertions.assertThat(actual.size).isEqualTo(2)
    }

    @Test
    fun `gets a single movement by id`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movement = Movement(
            12,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )

        whenever(movementRepository.findById(12)).thenReturn(Optional.of(movement))

        val actual = movementService.findById(12)

        verify(movementRepository).findById(12)
        Assertions.assertThat(actual).isNotEmpty
    }

    @Test
    fun `gets empty when movement id does not exist`() {
        whenever(movementRepository.findById(3)).thenReturn(Optional.empty())

        val actual = movementService.findById(3)

        verify(movementRepository).findById(3)
        Assertions.assertThat(actual).isEmpty
    }

    @Test
    fun `saves a new transfer`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movement = Movement(
            0,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )
        val movementDto = MovementDto(
            null,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location.id,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )

        whenever(locationRepository.findById(location.id)).thenReturn(Optional.of(location))
        whenever(mapper.toEntity(movementDto, location)).thenReturn(movement)
        whenever(movementRepository.save(movement)).thenReturn(movement.copy(id = 21))


        val actual = movementService.save(movementDto)

        verify(locationRepository).findById(location.id)
        verify(movementRepository).save(movement)
        Assertions.assertThat(actual).isEqualTo(movement.copy(id = 21))
    }

    @Test
    fun `cannot save a new movement when location does not exist`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movementDto = MovementDto(
            null,
            Money(300, Currency.EUR),
            MovementDirection.Inbound,
            location.id,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )

        whenever(locationRepository.findById(5)).thenReturn(Optional.empty())

        assertThrows(IllegalArgumentException::class.java) {
            movementService.save(movementDto)
        }

        verify(locationRepository).findById(5)
        verifyZeroInteractions(movementRepository)
    }

    @Test
    fun `updates an existing transfer`() {
        val location = Location(5, "Main", "Bankster", LocationType.BankAccount)
        val movement = Movement(
            12,
            Money(10, Currency.EUR),
            MovementDirection.Inbound,
            location,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )
        val movementDto = MovementDto(
            12,
            Money(10, Currency.EUR),
            MovementDirection.Inbound,
            location.id,
            MovementType.Wage,
            LocalDateTime.of(2021, 4, 8, 23, 56)
        )

        whenever(locationRepository.findById(location.id)).thenReturn(Optional.of(location))
        whenever(mapper.toEntity(movementDto, location)).thenReturn(movement)
        whenever(movementRepository.save(movement)).thenReturn(movement)


        val actual = movementService.save(movementDto)

        verify(locationRepository).findById(location.id)
        verify(movementRepository).save(movement)
        Assertions.assertThat(actual).isEqualTo(movement)
    }

    @Test
    fun `deletes a movement by id`() {
        movementService.deleteById(2)

        verify(movementRepository).deleteById(2)
    }
}