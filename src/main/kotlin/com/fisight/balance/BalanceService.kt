package com.fisight.balance

import com.fisight.currencyTrade.CurrencyTrade
import com.fisight.currencyTrade.CurrencyTradeService
import com.fisight.currencyTrade.CurrencyTradeType
import com.fisight.location.LocationRepository
import com.fisight.money.Money
import com.fisight.transfer.TransferService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BalanceService(
    private val locationRepository: LocationRepository,
    private val transferService: TransferService,
    private val currencyTradeService: CurrencyTradeService
) {
    fun calculateForLocation(locationId: Int, atDate: LocalDateTime): Balance {
        return locationRepository.findById(locationId)
            .map {
                val transferOperations = getTransferOperations(locationId, atDate)
                val currencyTradeOperations = getCurrencyTradeOperations(locationId, atDate)

                val allBalanceOperations = transferOperations + currencyTradeOperations
                Balance(calculateAggregatedBalance(allBalanceOperations), atDate)
            }
            .orElseThrow { throw IllegalArgumentException("Requested location does not exist") }
    }

    private fun calculateAggregatedBalance(allBalanceOperations: List<BalanceOperation>): List<Money> {
        return allBalanceOperations
            .asSequence()
            .sortedBy { it.dateExecuted }
            .map { it.amount }
            .groupingBy { it.currency }
            .reduce { _, accumulator, element -> accumulator.plus(element) }
            .values.toList()
    }

    private fun getTransferOperations(
        locationId: Int,
        atDate: LocalDateTime
    ) = transferService.findAllForLocationBeforeDate(locationId, atDate)
        .flatMap {
            val relativeAmount = if (it.source.id == locationId) it.amount.negate() else it.amount
            listOf(
                BalanceOperation(relativeAmount, it.dateTransferred),
                BalanceOperation(it.fee.negate(), it.dateTransferred)
            )
        }

    private fun getCurrencyTradeOperations(
        locationId: Int,
        atDate: LocalDateTime
    ) = currencyTradeService.findAllForLocationBeforeDate(locationId, atDate)
        .flatMap {
            if (it.tradeType == CurrencyTradeType.Buy) {
                balanceOperationsForCurrencyBuyTrade(it)
            } else {
                balanceOperationsForCurrencySellTrade(it)
            }
        }

    private fun balanceOperationsForCurrencySellTrade(currencyTrade: CurrencyTrade) = listOf(
        BalanceOperation(
            Money(currencyTrade.pricePerBaseUnit.times(currencyTrade.quantity).amount, currencyTrade.quoteCurrency),
            currencyTrade.dateTraded
        ),
        BalanceOperation(Money(currencyTrade.quantity, currencyTrade.baseCurrency).negate(), currencyTrade.dateTraded),
        BalanceOperation(currencyTrade.fee.negate(), currencyTrade.dateTraded)
    )

    private fun balanceOperationsForCurrencyBuyTrade(currencyTrade: CurrencyTrade) = listOf(
        BalanceOperation(
            Money(
                currencyTrade.pricePerBaseUnit.times(currencyTrade.quantity).negate().amount,
                currencyTrade.quoteCurrency
            ),
            currencyTrade.dateTraded
        ),
        BalanceOperation(Money(currencyTrade.quantity, currencyTrade.baseCurrency), currencyTrade.dateTraded),
        BalanceOperation(currencyTrade.fee.negate(), currencyTrade.dateTraded)
    )
}

data class BalanceOperation(val amount: Money, val dateExecuted: LocalDateTime)
