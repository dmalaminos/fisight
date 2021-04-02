package com.fisight.transfer

import com.fisight.location.Location
import com.fisight.money.Money
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Transfer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @ManyToOne(fetch = FetchType.EAGER)
    val source: Location,
    @ManyToOne(fetch = FetchType.EAGER)
    val target: Location,
    val amount: Money,
    val dateTransferred: LocalDateTime
)