package com.fisight.balance

import com.fisight.currencyTrade.CurrencyTrade
import com.fisight.currencyTrade.CurrencyTradeService
import com.fisight.currencyTrade.CurrencyTradeType
import com.fisight.location.Location
import com.fisight.location.LocationRepository
import com.fisight.money.Currency
import com.fisight.money.Money
import com.fisight.transfer.Transfer
import com.fisight.transfer.TransferService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*


class BalanceServiceTest {
    private lateinit var balanceService: BalanceService

    private val locationRepository: LocationRepository = mock()
    private val transferService: TransferService = mock()
    private val currencyTradeService: CurrencyTradeService = mock()

    @BeforeEach
    internal fun setUp() {
        balanceService = BalanceService(locationRepository, transferService, currencyTradeService)
    }

    @Test
    fun `cannot calculate balance by location when location does not exist`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            balanceService.calculateForLocation(999, LocalDateTime.of(2021, 4, 2, 23, 44))
        }

        verify(locationRepository).findById(999)
        verifyNoMoreInteractions(locationRepository)
    }

    @Test
    fun `calculates balance for location with transfers`() {
        val atDate = LocalDateTime.of(2021, 4, 2, 23, 44)
        val location21 = Location(21, "Another", "NuBank")
        val location42 = Location(42, "Main", "Bankster")
        val transfers = listOf(
            Transfer(
                1,
                location21,
                location42,
                Money(45, Currency.EUR),
                Money(0, Currency.EUR),
                LocalDateTime.of(2021, 2, 2, 11, 34)
            ),
            Transfer(
                2,
                location42,
                location21,
                Money(20, Currency.EUR),
                Money(0, Currency.EUR),
                LocalDateTime.of(2021, 3, 31, 14, 22)
            ),
            Transfer(
                3,
                location21,
                location42,
                Money(100, Currency.USD),
                Money(1.25, Currency.USD),
                LocalDateTime.of(2021, 4, 2, 23, 30)
            )
        )
        whenever(locationRepository.findById(42)).thenReturn(Optional.of(location42))
        whenever(transferService.findAllForLocationBeforeDate(42, atDate)).thenReturn(transfers)

        val balance = balanceService.calculateForLocation(42, atDate)

        verify(locationRepository).findById(42)
        verifyNoMoreInteractions(locationRepository)

        val expectedBalance = Balance(
            listOf(Money(25, Currency.EUR), Money(98.75, Currency.USD)),
            atDate
        )
        assertThat(balance).isEqualTo(expectedBalance)
    }

    @Test
    fun `calculates balance for location with no capital`() {
        val atDate = LocalDateTime.of(2021, 4, 2, 23, 44)
        val location42 = Location(42, "Main", "Bankster")
        whenever(locationRepository.findById(42)).thenReturn(Optional.of(location42))
        whenever(transferService.findAllForLocationBeforeDate(42, atDate)).thenReturn(emptyList())

        val balance = balanceService.calculateForLocation(42, atDate)

        verify(locationRepository).findById(42)
        verifyNoMoreInteractions(locationRepository)

        val expectedBalance = Balance(
            listOf(),
            atDate
        )
        assertThat(balance).isEqualTo(expectedBalance)
    }

    @Test
    fun `calculates balance for location with transfer and currency trades`() {
        val atDate = LocalDateTime.of(2021, 4, 2, 23, 44)
        val location21 = Location(21, "Another", "NuBank")
        val location42 = Location(42, "Main", "Bankster")
        val transfers = listOf(
            Transfer(
                1,
                location21,
                location42,
                Money(100.125, Currency.EUR),
                Money(2, Currency.EUR),
                LocalDateTime.of(2021, 2, 2, 11, 34)
            )
        )
        val currencyTrades = listOf(
            CurrencyTrade(
                1,
                Currency.BTC,
                Currency.EUR,
                CurrencyTradeType.Buy,
                Money(38409.48, Currency.EUR),
                0.000355,
                Money.zero(Currency.BTC),
                LocalDateTime.of(2021, 3, 3, 10, 44),
                location42
            ),
            CurrencyTrade(
                2,
                Currency.BTC,
                Currency.EUR,
                CurrencyTradeType.Sell,
                Money(40000, Currency.EUR),
                0.0002,
                Money.zero(Currency.BTC),
                LocalDateTime.of(2021, 3, 4, 10, 44),
                location42
            )
        )
        whenever(locationRepository.findById(42)).thenReturn(Optional.of(location42))
        whenever(transferService.findAllForLocationBeforeDate(42, atDate)).thenReturn(transfers)
        whenever(currencyTradeService.findAllForLocationBeforeDate(42, atDate)).thenReturn(currencyTrades)

        val balance = balanceService.calculateForLocation(42, atDate)

        verify(locationRepository).findById(42)
        verifyNoMoreInteractions(locationRepository)

        val expectedBalance = Balance(
            listOf(Money(92.48963460, Currency.EUR), Money(0.000155, Currency.BTC)),
            atDate
        )
        assertThat(balance).isEqualTo(expectedBalance)
    }
}