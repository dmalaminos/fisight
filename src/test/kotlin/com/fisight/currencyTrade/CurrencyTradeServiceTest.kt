package com.fisight.currencyTrade

import com.fisight.financialLocation.FinancialLocation
import com.fisight.financialLocation.FinancialLocationRepository
import com.fisight.money.Currency
import com.fisight.money.Money
import java.time.LocalDateTime
import java.util.*
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
    private val financialLocationRepository: FinancialLocationRepository = mock()

    @BeforeEach
    fun setUp() {
        currencyTradeService = CurrencyTradeService(currencyTradeRepository, financialLocationRepository)
    }

    @Test
    fun `gets all currency trades`() {
        val currencyTrades = listOf(
            CurrencyTrade(
                1,
                Currency.EUR,
                Currency.BTC,
                CurrencyTradeType.Buy,
                Money(1000, Currency.EUR),
                0.007,
                Money(3, Currency.EUR),
                LocalDateTime.of(2021, 3, 24, 23, 44),
                FinancialLocation(4, "MyExchange", "Bestexchange")
            ),
            CurrencyTrade(
                2,
                Currency.EUR,
                Currency.BTC,
                CurrencyTradeType.Sell,
                Money(200, Currency.EUR),
                0.007,
                Money(3, Currency.EUR),
                LocalDateTime.of(2021, 3, 24, 23, 50),
                FinancialLocation(4, "MyExchange", "Bestexchange")
            )
        )

        whenever(currencyTradeRepository.findAll()).thenReturn(currencyTrades)

        val actual = currencyTradeService.getAll()

        assertThat(actual.size).isEqualTo(2)
    }

    @Test
    fun `gets a single currency trade by id`() {
        val currencyTrade = CurrencyTrade(
            3,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            FinancialLocation(4, "MyExchange", "Bestexchange")
        )

        whenever(currencyTradeRepository.findById(3)).thenReturn(Optional.of(currencyTrade))

        val actual = currencyTradeService.getById(3)

        verify(currencyTradeRepository).findById(3)
        assertThat(actual).isNotEmpty
    }

    @Test
    fun `gets empty when currency trade id does not exist`() {
        whenever(currencyTradeRepository.findById(3)).thenReturn(Optional.empty())

        val actual = currencyTradeService.getById(3)

        verify(currencyTradeRepository).findById(3)
        assertThat(actual).isEmpty
    }

    @Test
    fun `saves a new currency trade`() {
        val currencyTradeDto = CurrencyTradeDto(
            null,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            null
        )

        val location = FinancialLocation(4, "MyExchange", "Bestexchange")
        val currencyTrade = CurrencyTrade(
            0,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            location
        )

        whenever(financialLocationRepository.findById(location.id)).thenReturn(Optional.of(location))
        whenever(currencyTradeRepository.save(currencyTrade)).thenReturn(currencyTrade.copy(id = 1))


        val actual = currencyTradeService.save(currencyTradeDto, 4)

        verify(financialLocationRepository).findById(location.id)
        verify(currencyTradeRepository).save(currencyTrade)
        assertThat(actual).isEqualTo(currencyTradeDto.copy(id = 1, location = location))
    }

    @Test
    fun `cannot save a new currency trade when location does not exist`() {
        val currencyTradeDto = CurrencyTradeDto(
            null,
            Currency.EUR,
            Currency.BTC,
            CurrencyTradeType.Buy,
            Money(1000, Currency.EUR),
            0.007,
            Money(3, Currency.EUR),
            LocalDateTime.of(2021, 3, 24, 23, 44),
            null
        )

        whenever(financialLocationRepository.findById(5)).thenReturn(Optional.empty())

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            currencyTradeService.save(currencyTradeDto, 5)
        }

        verify(financialLocationRepository).findById(5)
        verifyZeroInteractions(currencyTradeRepository)
    }

    @Test
    fun `deletes a currency trade by id`() {
        currencyTradeService.deleteById(2)

        verify(currencyTradeRepository).deleteById(2)
    }
}