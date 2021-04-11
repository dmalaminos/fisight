package com.fisight.location

import com.fisight.comment.Commentable
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Int,
    val name: String,
    val entityName: String,
    @Enumerated(EnumType.STRING)
    val type: LocationType
) : Commentable

enum class LocationType {
    Pocket,
    BankAccount,
    Broker,
    P2PLendingPlatform,
    CryptoExchange
}
