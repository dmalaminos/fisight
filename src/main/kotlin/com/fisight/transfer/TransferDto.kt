package com.fisight.transfer

import com.fisight.money.Money
import java.time.LocalDateTime

data class TransferDto(
    val id: Int?,
    val sourceLocationId: Int,
    val targetLocationId: Int,
    val amount: Money,
    val dateTransferred: LocalDateTime
)