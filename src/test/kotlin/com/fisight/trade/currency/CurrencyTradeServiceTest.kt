package com.fisight.trade.currency

import com.fisight.location.Location
import com.fisight.location.LocationRepository
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever

class CurrencyTradeServiceTest {
    private lateinit var currencyTradeService: CurrencyTradeService

    @Mock
    private val currencyTradeRepository: CurrencyTradeRepository = mock()

    @Mock
    private val locationRepository: LocationRepository = mock()

    @Mock
    private val currencyTradeMapper: CurrencyTradeMapper = mock()

    @BeforeEach
    fun setUp() {
        currencyTradeService = CurrencyTradeService(currencyTradeRepository, locationRepository, currencyTradeMapper)
    }

    @Test
    fun `gets all currency trades`() {
        val currencyTrades = listOf(
            CurrencyTrade(
                1,
                Currency.EUR,
                Currency.BTC,
                TradeType.Buy,
                Money(1000, Currency.EUR),
                0.007,
                Money(3, Currency.EUR),
                LocalDateTime.of(2021, 3, 24, 23, 44),
                Location(4, "MyExchange", "Bestexchange", LocationType.BankAccount)
            ),
            CurrencyTrade(
                2,
                Currency.EUR,
                Currency.BTC,
                TradeType.Sell,
                Money(200, Currency.EUR),
                0.007,
                Money(3, Currency.EUR),
                LocalDateTime.of(2021, 3, 24, 23, 50),
                Location(4, "MyExchange", "Bestexchange", LocationType.BankAccount)
            )
        )

        whenever(currencyTradeRepository.findAll()).thenReturn(currencyTrades)

        val actual = currencyTradeService.findAll()

        assertThat(actual.size).isEqualTo(2)
    }

    @Test
    fun `gets a single currency trade by id`() {
        val currencyTrade = CurrencyTrade(
            3,
            Currency.EUR,
            Currency.BTC,
            TradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            Location(4, "MyExchange", "Bestexchange", LocationType.BankAccount)
        )

        whenever(currencyTradeRepository.findById(3)).thenReturn(Optional.of(currencyTrade))

        val actual = currencyTradeService.findById(3)

        verify(currencyTradeRepository).findById(3)
        assertThat(actual).isNotEmpty
    }

    @Test
    fun `gets empty when currency trade id does not exist`() {
        whenever(currencyTradeRepository.findById(3)).thenReturn(Optional.empty())

        val actual = currencyTradeService.findById(3)

        verify(currencyTradeRepository).findById(3)
        assertThat(actual).isEmpty
    }

    @Test
    fun `saves a new currency trade`() {
        val currencyTradeDto = CurrencyTradeDto(
            null,
            Currency.EUR,
            Currency.BTC,
            TradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            4
        )

        val location = Location(4, "MyExchange", "Bestexchange", LocationType.BankAccount)
        val currencyTrade = CurrencyTrade(
            0,
            Currency.EUR,
            Currency.BTC,
            TradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            location
        )

        whenever(locationRepository.findById(location.id)).thenReturn(Optional.of(location))
        whenever(currencyTradeMapper.toEntity(currencyTradeDto, location)).thenReturn(currencyTrade)
        whenever(currencyTradeRepository.save(currencyTrade)).thenReturn(currencyTrade.copy(id = 1))


        val actual = currencyTradeService.save(currencyTradeDto)

        verify(locationRepository).findById(location.id)
        verify(currencyTradeRepository).save(currencyTrade)
        assertThat(actual).isEqualTo(currencyTrade.copy(id = 1))
    }

    @Test
    fun `cannot save a new currency trade when location does not exist`() {
        val currencyTradeDto = CurrencyTradeDto(
            null,
            Currency.EUR,
            Currency.BTC,
            TradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            5
        )

        whenever(locationRepository.findById(5)).thenReturn(Optional.empty())

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            currencyTradeService.save(currencyTradeDto)
        }

        verify(locationRepository).findById(5)
        verifyZeroInteractions(currencyTradeRepository)
    }

    @Test
    fun `deletes a currency trade by id`() {
        currencyTradeService.deleteById(2)

        verify(currencyTradeRepository).deleteById(2)
    }
}