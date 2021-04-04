package com.fisight.balance

import com.fisight.money.Money
import java.time.LocalDateTime

data class Balance(val amounts: List<Money>, val dateOfBalance: LocalDateTime)
