package com.fisight.transfer

import com.fisight.location.Location
import com.fisight.location.LocationRepository
import com.fisight.money.Currency
import com.fisight.money.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*

class TransferServiceTest {
    private lateinit var transferService: TransferService

    @Mock
    private val transferRepository: TransferRepository = mock()

    @Mock
    private val locationRepository: LocationRepository = mock()


    @BeforeEach
    fun setUp() {
        transferService = TransferService(transferRepository, locationRepository)
    }

    @Test
    fun `gets all transfers`() {
        val transfers = listOf(
            Transfer(
                1,
                Location(5, "Primary bank", "Bankster"),
                Location(8, "Secondary bank", "NuBank"),
                Money(100, Currency.EUR),
                LocalDateTime.of(2021, 4, 2, 17, 21)
            ),
            Transfer(
                3,
                Location(8, "Secondary bank", "NuBank"),
                Location(5, "Primary bank", "Bankster"),
                Money(5, Currency.EUR),
                LocalDateTime.of(2021, 4, 2, 20, 21)
            )
        )

        whenever(transferRepository.findAll()).thenReturn(transfers)

        val actual = transferService.findAll()

        assertThat(actual.size).isEqualTo(2)
    }

    @Test
    fun `gets all transfers by source location`() {
        val transfers = listOf(
            Transfer(
                2,
                Location(8, "Secondary bank", "NuBank"),
                Location(5, "Primary bank", "Bankster"),
                Money(200, Currency.EUR),
                LocalDateTime.of(2021, 2, 26, 12, 38)
            ),
            Transfer(
                3,
                Location(8, "Secondary bank", "NuBank"),
                Location(5, "Primary bank", "Bankster"),
                Money(5, Currency.EUR),
                LocalDateTime.of(2021, 4, 2, 20, 21)
            )
        )

        whenever(transferRepository.findBySource_Id(8)).thenReturn(transfers)

        val actual = transferService.findAllBySourceLocation(8)

        assertThat(actual.size).isEqualTo(2)
    }

    @Test
    fun `gets a single transfer by id`() {
        val transfer = Transfer(
            1,
            Location(5, "Primary bank", "Bankster"),
            Location(8, "Secondary bank", "NuBank"),
            Money(100, Currency.EUR),
            LocalDateTime.of(2021, 4, 2, 17, 21)
        )

        whenever(transferRepository.findById(1)).thenReturn(Optional.of(transfer))

        val actual = transferService.findById(1)

        verify(transferRepository).findById(1)
        assertThat(actual).isNotEmpty
    }

    @Test
    fun `gets empty when transfer id does not exist`() {
        whenever(transferRepository.findById(3)).thenReturn(Optional.empty())

        val actual = transferService.findById(3)

        verify(transferRepository).findById(3)
        assertThat(actual).isEmpty
    }

    @Test
    fun `saves a new transfer`() {
        val transferDto = TransferDto(
            null,
            5,
            8,
            Money(100, Currency.EUR),
            LocalDateTime.of(2021, 4, 2, 17, 21)
        )

        val sourceLocation = Location(5, "Primary bank", "Bankster")
        val targetLocation = Location(8, "Secondary bank", "NuBank")
        val transfer = Transfer(
            0,
            sourceLocation,
            targetLocation,
            Money(100, Currency.EUR),
            LocalDateTime.of(2021, 4, 2, 17, 21)
        )

        whenever(locationRepository.findById(sourceLocation.id)).thenReturn(Optional.of(sourceLocation))
        whenever(locationRepository.findById(targetLocation.id)).thenReturn(Optional.of(targetLocation))
        whenever(transferRepository.save(transfer)).thenReturn(transfer.copy(id = 21))


        val actual = transferService.save(transferDto)

        verify(locationRepository).findById(sourceLocation.id)
        verify(locationRepository).findById(targetLocation.id)
        verify(transferRepository).save(transfer)
        assertThat(actual).isEqualTo(transferDto.copy(id = 21))
    }

    @Test
    fun `cannot save a new transfer when location does not exist`() {
        val transferDto = TransferDto(
            null,
            5,
            88,
            Money(100, Currency.EUR),
            LocalDateTime.of(2021, 4, 2, 17, 21)
        )

        val sourceLocation = Location(5, "Primary bank", "Bankster")
        whenever(locationRepository.findById(5)).thenReturn(Optional.of(sourceLocation))
        whenever(locationRepository.findById(88)).thenReturn(Optional.empty())

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            transferService.save(transferDto)
        }

        verify(locationRepository).findById(5)
        verify(locationRepository).findById(88)
        verifyZeroInteractions(transferRepository)
    }

    @Test
    fun `deletes a transfer by id`() {
        transferService.deleteById(2)

        verify(transferRepository).deleteById(2)
    }
}