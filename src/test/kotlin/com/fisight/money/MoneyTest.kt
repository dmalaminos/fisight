package com.fisight.money

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal

class MoneyTest {
    @Test
    fun `instantiates money with the correct amount`() {
        assertEquals(BigDecimal.valueOf(1.23), Money(BigDecimal.valueOf(1.23), Currency.EUR).amount)
        assertEquals(BigDecimal(5), Money(5, Currency.ADA).amount)
        assertEquals(BigDecimal.ZERO, Money.zero(Currency.EUR).amount)
    }

    @ParameterizedTest
    @CsvSource(
        "1.23, 0.0, 1.23",
        "1.23, 3.21, 4.44",
        "1.23, -3.21, -1.98",
        "1.23, 5.74291, 6.97291"
    )
    fun `sums amounts of money`(amount1: Double, amount2: Double, result: Double) {
        assertEquals(Money(result, Currency.EUR), Money(amount1, Currency.EUR) + Money(amount2, Currency.EUR))
    }

    @Test
    fun `does not sum money with different currencies`() {
        assertThrows(IllegalArgumentException::class.java) { Money(1.23, Currency.EUR) + Money(3.21, Currency.USD) }
    }

    @ParameterizedTest
    @CsvSource(
        "1.23, 0.0, 1.23",
        "1.23, 3.21, -1.98",
        "1.23, -3.21, 4.44",
        "1.23, 5.74291, -4.51291"
    )
    fun `subtracts amounts of money`(amount1: Double, amount2: Double, result: Double) {
        assertEquals(Money(result, Currency.EUR), Money(amount1, Currency.EUR) - Money(amount2, Currency.EUR))
    }

    @Test
    fun `does not subtract money with different currencies`() {
        assertThrows(IllegalArgumentException::class.java) { Money(1.23, Currency.EUR) - Money(3.21, Currency.USD) }
    }

    @Test
    fun `compares amounts of money`() {
        assertTrue((Money(3.21, Currency.EUR) > Money(1.23, Currency.EUR)))
        assertTrue((Money(1.23, Currency.EUR) >= Money(1.23, Currency.EUR)))
        assertTrue((Money(3.21, Currency.EUR) >= Money(1.23, Currency.EUR)))
        assertTrue((Money(1.23, Currency.EUR) < Money(3.21, Currency.EUR)))
        assertTrue((Money(1.23, Currency.EUR) <= Money(1.23, Currency.EUR)))
        assertTrue((Money(1.23, Currency.EUR) <= Money(3.21, Currency.EUR)))
        assertTrue((Money(1.23, Currency.EUR) == Money(1.23, Currency.EUR)))
        assertTrue((Money(1.23, Currency.EUR) != Money(3.21, Currency.EUR)))
    }
}