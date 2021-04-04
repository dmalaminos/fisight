package com.fisight.money

import java.math.BigDecimal
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class Money(val amount: BigDecimal, @Enumerated(EnumType.STRING) val currency: Currency) {
    constructor(amount: Int, currency: Currency) : this(BigDecimal(amount), currency)
    constructor(amount: Double, currency: Currency) : this(BigDecimal.valueOf(amount), currency)

    companion object {
        fun zero(currency: Currency) = Money(BigDecimal.ZERO, currency)
    }

    operator fun plus(money: Money): Money = isSameCurrency(money) { copy(amount = this.amount + money.amount) }
    operator fun minus(money: Money): Money = isSameCurrency(money) { copy(amount = this.amount - money.amount) }
    operator fun times(factor: Int): Money = this * BigDecimal(factor)
    operator fun times(factor: Double): Money = this * BigDecimal.valueOf(factor)
    operator fun times(factor: BigDecimal): Money = copy(amount = (this.amount * factor).stripTrailingZeros())
    operator fun compareTo(money: Money): Int = isSameCurrency(money) { this.amount.compareTo(money.amount) }

    fun negate() = copy(amount = this.amount.negate())

    private fun <T> isSameCurrency(money: Money, operation: (Money) -> T): T {
        return if (currency == money.currency) {
            operation(money)
        } else {
            throw IllegalArgumentException("Cannot operate with mismatching currencies: $currency, ${money.currency}")
        }
    }
}
