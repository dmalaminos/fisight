package com.fisight.trade.share

import com.fisight.location.Location
import com.fisight.location.LocationRepository
import com.fisight.location.LocationType
import com.fisight.money.Currency
import com.fisight.money.Money
import com.fisight.trade.currency.TradeType
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


class ShareTradeServiceTest {
    private lateinit var shareTradeService: ShareTradeService

    @Mock
    private val shareTradeRepository: ShareTradeRepository = mock()

    @Mock
    private val locationRepository: LocationRepository = mock()

    @Mock
    private val shareTradeMapper: ShareTradeMapper = mock()

    @BeforeEach
    fun setUp() {
        shareTradeService = ShareTradeService(shareTradeRepository, locationRepository, shareTradeMapper)
    }

    @Test
    fun `gets all share trades`() {
        val shareTrades = listOf(
            ShareTrade(
                1,
                "World ETF",
                "isin-code",
                "MyExchange",
                "ME:WETF",
                TradeType.Buy,
                Currency.EUR,
                Currency.EUR,
                Money(80.78, Currency.EUR),
                256.1,
                Money(5, Currency.EUR),
                LocalDateTime.of(2021, 4, 11, 22, 16),
                Location(4, "MyExchange", "Bestexchange", LocationType.Broker)
            ),
            ShareTrade(
                2,
                "World ETF",
                "isin-code",
                "MyExchange",
                "ME:WETF",
                TradeType.Sell,
                Currency.EUR,
                Currency.EUR,
                Money(85.78, Currency.EUR),
                64.2,
                Money(5, Currency.EUR),
                LocalDateTime.of(2021, 4, 11, 23, 56),
                Location(4, "MyExchange", "Bestexchange", LocationType.Broker)
            )
        )

        whenever(shareTradeRepository.findAll()).thenReturn(shareTrades)

        val actual = shareTradeService.findAll()

        Assertions.assertThat(actual.size).isEqualTo(2)
    }

    @Test
    fun `gets a single share trade by id`() {
        val shareTrade = ShareTrade(
            3,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            Location(4, "MyExchange", "Bestexchange", LocationType.Broker)
        )

        whenever(shareTradeRepository.findById(3)).thenReturn(Optional.of(shareTrade))

        val actual = shareTradeService.findById(3)

        verify(shareTradeRepository).findById(3)
        Assertions.assertThat(actual).isNotEmpty
    }

    @Test
    fun `gets empty when share trade id does not exist`() {
        whenever(shareTradeRepository.findById(3)).thenReturn(Optional.empty())

        val actual = shareTradeService.findById(3)

        verify(shareTradeRepository).findById(3)
        Assertions.assertThat(actual).isEmpty
    }

    @Test
    fun `saves a new share trade`() {
        val location = Location(4, "MyExchange", "Bestexchange", LocationType.Broker)
        val shareTradeDto = ShareTradeDto(
            null,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            location.id
        )

        val shareTrade = ShareTrade(
            0,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            location
        )

        whenever(locationRepository.findById(location.id)).thenReturn(Optional.of(location))
        whenever(shareTradeMapper.toEntity(shareTradeDto, location)).thenReturn(shareTrade)
        whenever(shareTradeRepository.save(shareTrade)).thenReturn(shareTrade.copy(id = 1))


        val actual = shareTradeService.save(shareTradeDto)

        verify(locationRepository).findById(location.id)
        verify(shareTradeRepository).save(shareTrade)
        Assertions.assertThat(actual).isEqualTo(shareTrade.copy(id = 1))
    }

    @Test
    fun `cannot save a new share trade when location does not exist`() {
        val shareTradeDto = ShareTradeDto(
            null,
            "World ETF",
            "isin-code",
            "MyExchange",
            "ME:WETF",
            TradeType.Buy,
            Currency.EUR,
            Currency.EUR,
            Money(80.78, Currency.EUR),
            256.1,
            Money(5, Currency.EUR),
            LocalDateTime.of(2021, 4, 11, 22, 16),
            5
        )

        whenever(locationRepository.findById(5)).thenReturn(Optional.empty())

        assertThrows(IllegalArgumentException::class.java) {
            shareTradeService.save(shareTradeDto)
        }

        verify(locationRepository).findById(5)
        verifyZeroInteractions(shareTradeRepository)
    }

    @Test
    fun `deletes a share trade by id`() {
        shareTradeService.deleteById(2)

        verify(shareTradeRepository).deleteById(2)
    }
}